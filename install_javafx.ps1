# JavaFX SDK 자동 설치 스크립트
# 이 스크립트는 JavaFX SDK를 다운로드하고 프로젝트에 설치합니다.

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "JavaFX SDK 자동 설치 시작" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 변수 설정
$javafxVersion = "17.0.12"
$downloadUrl = "https://download2.gluonhq.com/openjfx/$javafxVersion/openjfx-${javafxVersion}_windows-x64_bin-sdk.zip"
$tempDir = "$env:TEMP\javafx-install"
$zipFile = "$tempDir\javafx-sdk.zip"
$extractDir = "$tempDir\javafx-sdk"
$installDir = "C:\javafx-sdk-17"
$projectLibDir = "$PSScriptRoot\lib"

# 1. 임시 디렉토리 생성
Write-Host "[1/6] 임시 디렉토리 생성 중..." -ForegroundColor Yellow
if (Test-Path $tempDir) {
    Remove-Item $tempDir -Recurse -Force
}
New-Item -ItemType Directory -Path $tempDir -Force | Out-Null
Write-Host "✓ 완료" -ForegroundColor Green
Write-Host ""

# 2. JavaFX SDK 다운로드
Write-Host "[2/6] JavaFX SDK $javafxVersion 다운로드 중..." -ForegroundColor Yellow
Write-Host "URL: $downloadUrl" -ForegroundColor Gray
try {
    Invoke-WebRequest -Uri $downloadUrl -OutFile $zipFile -UseBasicParsing
    Write-Host "✓ 다운로드 완료 ($('{0:N2}' -f ((Get-Item $zipFile).Length / 1MB)) MB)" -ForegroundColor Green
} catch {
    Write-Host "✗ 다운로드 실패: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 3. 압축 해제
Write-Host "[3/6] 압축 해제 중..." -ForegroundColor Yellow
try {
    Expand-Archive -Path $zipFile -DestinationPath $extractDir -Force
    Write-Host "✓ 압축 해제 완료" -ForegroundColor Green
} catch {
    Write-Host "✗ 압축 해제 실패: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 4. JavaFX SDK를 C:\javafx-sdk-17로 이동
Write-Host "[4/6] JavaFX SDK를 $installDir 로 설치 중..." -ForegroundColor Yellow
try {
    if (Test-Path $installDir) {
        Write-Host "기존 설치 디렉토리 삭제 중..." -ForegroundColor Gray
        Remove-Item $installDir -Recurse -Force
    }
    
    $sdkFolder = Get-ChildItem -Path $extractDir -Directory | Select-Object -First 1
    Move-Item -Path $sdkFolder.FullName -Destination $installDir -Force
    Write-Host "✓ 설치 완료: $installDir" -ForegroundColor Green
} catch {
    Write-Host "✗ 설치 실패: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 5. lib 폴더에 JAR 파일 복사
Write-Host "[5/6] 프로젝트 lib 폴더로 JAR 복사 중..." -ForegroundColor Yellow
if (-not (Test-Path $projectLibDir)) {
    New-Item -ItemType Directory -Path $projectLibDir -Force | Out-Null
}

$jarFiles = @(
    "javafx.base.jar",
    "javafx.controls.jar",
    "javafx.fxml.jar",
    "javafx.graphics.jar",
    "javafx.media.jar",
    "javafx.swing.jar",
    "javafx.web.jar"
)

$copiedCount = 0
foreach ($jar in $jarFiles) {
    $sourcePath = "$installDir\lib\$jar"
    $destPath = "$projectLibDir\$jar"
    
    if (Test-Path $sourcePath) {
        Copy-Item -Path $sourcePath -Destination $destPath -Force
        Write-Host "  ✓ $jar" -ForegroundColor Green
        $copiedCount++
    } else {
        Write-Host "  ✗ $jar (파일을 찾을 수 없음)" -ForegroundColor Red
    }
}
Write-Host "✓ $copiedCount 개 파일 복사 완료" -ForegroundColor Green
Write-Host ""

# 6. 정리
Write-Host "[6/6] 임시 파일 정리 중..." -ForegroundColor Yellow
Remove-Item $tempDir -Recurse -Force
Write-Host "✓ 정리 완료" -ForegroundColor Green
Write-Host ""

# 완료 메시지
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "JavaFX 설치 완료!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "설치 위치: $installDir" -ForegroundColor White
Write-Host "프로젝트 lib: $projectLibDir" -ForegroundColor White
Write-Host ""
Write-Host "다음 단계:" -ForegroundColor Yellow
Write-Host "1. VS Code를 재시작하세요" -ForegroundColor White
Write-Host "2. Java Language Server를 새로고침하세요 (Ctrl+Shift+P -> 'Java: Clean Java Language Server Workspace')" -ForegroundColor White
Write-Host "3. 애플리케이션을 다시 실행하세요" -ForegroundColor White
Write-Host ""
Write-Host "실행 시 필요한 VM Arguments:" -ForegroundColor Yellow
Write-Host '--module-path "C:\javafx-sdk-17\lib" --add-modules javafx.controls,javafx.web,javafx.swing' -ForegroundColor Gray
Write-Host ""
