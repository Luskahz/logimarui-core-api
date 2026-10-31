# Loaded by the integrated PROD launcher under its lifecycle lock.
# Build beside the active JAR; replace it only after owned processes have stopped.
function Ensure-ProductionCoreBuild {
    $release=(Get-Date -Format 'yyyyMMdd-HHmmss')+'-'+[guid]::NewGuid().ToString('N').Substring(0,8)
    $directory=Join-Path $core ('target/core-builds/'+$release)
    New-Item -ItemType Directory -Path $directory -Force | Out-Null
    $script:pendingCoreArtifact=Join-Path $directory 'logimarui-core-api-0.0.1-SNAPSHOT.jar'
    $script:previousCoreArtifact=Join-Path $directory 'previous-core.jar'
    $buildLog=Join-Path $private ('core-build-'+$release+'.log')
    $errorLog=Join-Path $private ('core-build-'+$release+'.err.log')
    $env:JAVA_HOME=$javaHome
    Write-Output ('Compilando backend atualizado em pasta isolada; servidor atual preservado. Log: '+$buildLog)
    $command='mvnw.cmd -q -DskipTests "-Dlogimarui.build.directory='+$directory+'" package'
    $builder=Start-Process -FilePath $env:ComSpec -ArgumentList @('/d','/c',$command) -WorkingDirectory $core -WindowStyle Hidden -PassThru -RedirectStandardOutput $buildLog -RedirectStandardError $errorLog
    $null=$builder.Handle
    $deadline=(Get-Date).AddMinutes(15)
    $checkpoint=Get-Date
    while(!$builder.WaitForExit(1000)){
        if((Get-Date) -gt $deadline){
            & taskkill.exe /PID $builder.Id /T /F *> $null
            throw ('Timeout no build do backend. Servidor atual preservado. Consulte '+$buildLog)
        }
        if(((Get-Date)-$checkpoint).TotalSeconds -ge 15){Write-Output 'Compilando backend; aguardando conclusao...';$checkpoint=Get-Date}
    }
    $builder.Refresh()
    if($builder.ExitCode -ne 0 -or !(Test-Path -LiteralPath $script:pendingCoreArtifact)){
        throw ('Falha ao gerar o backend. Servidor atual preservado. Consulte '+$buildLog+' e '+$errorLog)
    }
    $script:pendingCoreHash=(Get-FileHash -LiteralPath $script:pendingCoreArtifact -Algorithm SHA256).Hash
    @{createdAt=(Get-Date -Format o);workspace=$core;sha256=$script:pendingCoreHash} | ConvertTo-Json |
        Set-Content -LiteralPath (Join-Path $directory 'build.json') -Encoding UTF8
    Write-Output ('Backend atualizado compilado: '+$release+'.')
}

function Install-ProductionCoreArtifact {
    $artifact=Join-Path $core 'target/logimarui-core-api-0.0.1-SNAPSHOT.jar'
    if(!(Test-Path -LiteralPath $script:pendingCoreArtifact) -or
        (Get-FileHash -LiteralPath $script:pendingCoreArtifact -Algorithm SHA256).Hash -ne $script:pendingCoreHash){
        throw 'Backend preparado ausente ou alterado; ativacao cancelada.'
    }
    $hadPrevious=Test-Path -LiteralPath $artifact
    if($hadPrevious){Copy-Item -LiteralPath $artifact -Destination $script:previousCoreArtifact}
    try{
        Copy-Item -LiteralPath $script:pendingCoreArtifact -Destination $artifact -Force
        if((Get-FileHash -LiteralPath $artifact -Algorithm SHA256).Hash -ne $script:pendingCoreHash){
            throw 'O backend copiado nao corresponde ao build preparado.'
        }
    }catch{
        if($hadPrevious){Copy-Item -LiteralPath $script:previousCoreArtifact -Destination $artifact -Force}
        throw
    }
    Write-Output 'Backend atualizado instalado. Iniciando a versao compilada junto com o frontend...'
}
