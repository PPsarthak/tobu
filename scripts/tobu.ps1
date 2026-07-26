$ErrorActionPreference = "Stop"

# --------------------------------------------------
# Configuration
# --------------------------------------------------

$JarPath = Join-Path $PSScriptRoot "..\target\tobu-1.1.jar"
$JarPath = [System.IO.Path]::GetFullPath($JarPath)

$MinimumJavaVersion = 17

# --------------------------------------------------
# Validate JAR
# --------------------------------------------------

if (-not (Test-Path $JarPath)) {
    Write-Host "ERROR: Tobu JAR not found." -ForegroundColor Red
    Write-Host "Expected location:"
    Write-Host $JarPath
    Write-Host ""
    Write-Host "Run 'mvn clean package -DskipTests' first."
    exit 1
}

# --------------------------------------------------
# Find Java
# --------------------------------------------------

$JavaCandidates = @()

# 1. Java available through PATH
$JavaCommand = Get-Command java.exe -ErrorAction SilentlyContinue

if ($null -ne $JavaCommand) {
    $JavaCandidates += $JavaCommand.Source
}

# 2. JAVA_HOME
if ($env:JAVA_HOME) {

    $JavaHomeJava = Join-Path `
        $env:JAVA_HOME `
        "bin\java.exe"

    if (Test-Path $JavaHomeJava) {
        $JavaCandidates += $JavaHomeJava
    }
}

# 3. Common Java installation locations
$CommonJavaDirectories = @(
    "C:\Program Files\Java",
    "C:\Program Files\Eclipse Adoptium",
    "C:\Program Files\RedHat",
    "C:\Program Files\Microsoft",
    "C:\Program Files\Amazon Corretto"
)

foreach ($Directory in $CommonJavaDirectories) {

    if (Test-Path $Directory) {

        $JavaCandidates += Get-ChildItem `
            -Path $Directory `
            -Directory `
            -ErrorAction SilentlyContinue |
            ForEach-Object {

                $Candidate = Join-Path `
                    $_.FullName `
                    "bin\java.exe"

                if (Test-Path $Candidate) {
                    $Candidate
                }
            }
    }
}

# Remove duplicate paths
$JavaCandidates = $JavaCandidates |
    Select-Object -Unique

# --------------------------------------------------
# Find suitable Java version
# --------------------------------------------------

# --------------------------------------------------
# Find suitable Java version
# --------------------------------------------------

$SelectedJava = $null

foreach ($Java in $JavaCandidates) {

    Write-Host "Checking Java: $Java"

    try {

        $ProcessInfo = New-Object System.Diagnostics.ProcessStartInfo

        $ProcessInfo.FileName = $Java
        $ProcessInfo.Arguments = "-version"
        $ProcessInfo.RedirectStandardError = $true
        $ProcessInfo.RedirectStandardOutput = $true
        $ProcessInfo.UseShellExecute = $false
        $ProcessInfo.CreateNoWindow = $true

        $Process = New-Object System.Diagnostics.Process

        $Process.StartInfo = $ProcessInfo

        $Process.Start() | Out-Null

        $StandardOutput = $Process.StandardOutput.ReadToEnd()
        $StandardError = $Process.StandardError.ReadToEnd()

        $Process.WaitForExit()

        $VersionOutput = $StandardOutput + $StandardError

        if ($VersionOutput -match 'version\s+"(\d+)') {

            $MajorVersion = [int]$Matches[1]

            Write-Host "Detected Java version: $MajorVersion"

            if ($MajorVersion -ge $MinimumJavaVersion) {

                $SelectedJava = $Java

                Write-Host `
                    "Selected Java: $SelectedJava" `
                    -ForegroundColor Green

                break
            }
        }
    }
    catch {

        Write-Host `
            "Unable to check Java at: $Java" `
            -ForegroundColor Yellow
    }
}

# --------------------------------------------------
# No suitable Java found
# --------------------------------------------------

if ($null -eq $SelectedJava) {

    Write-Host `
        "ERROR: Could not find Java $MinimumJavaVersion or newer." `
        -ForegroundColor Red

    Write-Host ""
    Write-Host "Tobu requires Java $MinimumJavaVersion or newer."

    exit 1
}


# --------------------------------------------------
# Run Tobu
# --------------------------------------------------

Write-Host "Using Java: $SelectedJava" -ForegroundColor Cyan

& $SelectedJava `
    -jar $JarPath `
    $args

exit $LASTEXITCODE