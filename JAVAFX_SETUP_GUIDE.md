# JavaFX 설치 및 설정 가이드

## 📦 필요한 이유

`LocationView.java`에서 실제 네이버 지도를 표시하기 위해 **JavaFX WebView**를 사용합니다.  
JDK 11부터 JavaFX는 별도 모듈로 분리되어 추가 설치가 필요합니다.

---

## 🚀 설치 방법

### 방법 1: JavaFX SDK 다운로드 (권장)

#### 1단계: JavaFX SDK 다운로드
1. [Gluon JavaFX 다운로드 페이지](https://gluonhq.com/products/javafx/) 방문
2. **SDK** 탭 선택
3. Java 버전: **17** 선택
4. OS: **Windows** 선택
5. **JavaFX Windows SDK 17** 다운로드

또는 직접 링크:
```
https://download2.gluonhq.com/openjfx/17.0.12/openjfx-17.0.12_windows-x64_bin-sdk.zip
```

#### 2단계: 압축 해제
다운로드한 zip 파일을 원하는 위치에 압축 해제합니다.
예: `C:\javafx-sdk-17`

#### 3단계: lib 폴더에 JAR 파일 복사
압축 해제한 폴더의 `lib` 디렉토리에서 다음 파일들을 프로젝트의 `lib` 폴더로 복사:

```
javafx-sdk-17/lib/javafx.base.jar
javafx-sdk-17/lib/javafx.controls.jar
javafx-sdk-17/lib/javafx.fxml.jar
javafx-sdk-17/lib/javafx.graphics.jar
javafx-sdk-17/lib/javafx.media.jar
javafx-sdk-17/lib/javafx.swing.jar
javafx-sdk-17/lib/javafx.web.jar
```

복사 대상:
```
c:\Users\tkdwl\Desktop\H_Star\HongStargram\lib\
```

#### 4단계: VS Code 설정 업데이트

`.vscode/settings.json` 또는 프로젝트 설정에서 classpath에 JavaFX 추가:

```json
{
    "java.project.referencedLibraries": [
        "lib/**/*.jar",
        "C:\\javafx-sdk-17\\lib\\**/*.jar"
    ]
}
```

---

### 방법 2: Maven/Gradle 사용 (선택)

만약 Maven이나 Gradle을 사용한다면 의존성 추가:

**Maven (pom.xml):**
```xml
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17.0.12</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-web</artifactId>
        <version>17.0.12</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-swing</artifactId>
        <version>17.0.12</version>
    </dependency>
</dependencies>
```

**Gradle (build.gradle):**
```gradle
dependencies {
    implementation 'org.openjfx:javafx-controls:17.0.12'
    implementation 'org.openjfx:javafx-web:17.0.12'
    implementation 'org.openjfx:javafx-swing:17.0.12'
}
```

---

## ▶️ 실행 설정

### VM Arguments 추가

JavaFX 모듈을 사용하려면 실행 시 VM arguments를 추가해야 합니다:

```
--module-path "C:\javafx-sdk-17\lib" --add-modules javafx.controls,javafx.web,javafx.swing
```

#### VS Code launch.json 설정 예시:
```json
{
    "type": "java",
    "name": "Launch Front App",
    "request": "launch",
    "mainClass": "Front.App.App",
    "vmArgs": "--module-path \"C:\\javafx-sdk-17\\lib\" --add-modules javafx.controls,javafx.web,javafx.swing"
}
```

#### 명령줄 실행:
```powershell
java --module-path "C:\javafx-sdk-17\lib" --add-modules javafx.controls,javafx.web,javafx.swing -cp . Front.App.App
```

---

## ✅ 설치 확인

설치 후 다음 코드가 에러 없이 컴파일되어야 합니다:

```java
import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
```

---

## 🎯 완료 후 작업

1. JavaFX SDK 다운로드 및 설치
2. JAR 파일을 `lib` 폴더에 복사
3. VS Code에서 프로젝트 새로고침
4. 애플리케이션 재실행

그러면 **실제 네이버 지도**가 화면에 표시됩니다! 🗺️

---

## 🔧 문제 해결

### "javafx cannot be resolved to a type" 에러가 계속 발생하는 경우:
1. VS Code 재시작
2. Java Language Server 재시작 (`Ctrl+Shift+P` → "Java: Clean Java Language Server Workspace")
3. `.vscode/settings.json`의 `java.project.referencedLibraries` 확인

### 런타임 에러가 발생하는 경우:
- VM arguments에 `--module-path`와 `--add-modules`가 올바르게 설정되었는지 확인
- JavaFX SDK 경로가 정확한지 확인

---

**작성일**: 2025-12-09  
**작성자**: 프론트엔드 팀
