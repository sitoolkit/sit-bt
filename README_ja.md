[English](README.md)

# Batch Translator

Batch Translator はテキストファイルを翻訳する CLI ツールです。
想定する主な利用者は、README やドキュメントを翻訳する必要のある OSS 開発プロジェクトメンバーです。
主な使用方法は以下の通りです。

- 1 つのファイルの翻訳 (README.md など)
- ディレクトリ以下の特定の拡張子を持ったファイルの一括翻訳 ([AsciiDoctor](https://asciidoctor.org/)を使ったドキュメントなど)

## 利用者向け

### 実行環境

Batch Translator を実行するには以下のソフトウェアが必要です。

- Java 11+
- Maven 3.6+ (Maven Plugin として使用する場合)

#### 使用方法

Batch Translator は Java コマンド、または Maven Plugin として実行します。

1. [API Key の作成](#API Key の作成)
1. [Java コマンドで実行](Java コマンドで実行) or [Maven Plugin として実行](Maven Plugin として実行)

#### API Key の作成

Batch Translator は以下の翻訳エンジンを使用しています。
* みんなの自動翻訳＠TexTra®
* Amazon Translate
* Azure翻訳

翻訳機能を使用するには以下のいずれかのサイトでアカウントを作成してください。

##### みんなの自動翻訳＠TexTra® を使用する場合

https://mt-auto-minhon-mlt.ucri.jgn-x.jp/

アカウントを作成したら[設定ページ](https://mt-auto-minhon-mlt.ucri.jgn-x.jp/content/setting/user/edit/)にあるユーザー ID、API KEY、API SECRET を sit-at.properties ファイルに保存します。

sit-at.properties ファイルはユーザーホームディレクトリ以下に.sitoolkit という名前のディレクトリを作成し、そこに保存してください。

- Windows

```bat
mkdir %USERPROFILE%\.sitoolkit
notepad %USERPROFILE%\.sitoolkit\sit-bt.properties
```

- macOs

```sh
mkdir ~/.sitoolkit
nano ~/.sitoolkit/sit-bt.properties
```

- sit-at.properties

```properties
api_key=your_api_key
api_secret=your_api_secret
name=your_user_name
```

##### Amazon Translate を使用する場合

https://portal.aws.amazon.com/billing/signup#/start

アカウントを作成したら[ユーザガイド](https://docs.aws.amazon.com/ja_jp/cli/latest/userguide/cli-configure-files.html)を参考に、aws_access_key_id と aws_secret_access_key を credentials ファイルに保存します。

credentials ファイルは、ホームディレクトリ の .awsフォルダ の配下に作成してください。

Ex. ~/.aws/credentials
```properties
[default]
aws_access_key_id=AKIAIOSFODNN7EXAMPLE
aws_secret_access_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
```

##### Azure翻訳 を使用する場合

加筆中

#### Java コマンドで実行

```
curl -o sit-bt-core-0.9.jar https://repo1.maven.org/maven2/io/sitoolkit/bt/sit-bt-core/0.9/sit-bt-core-0.9.jar

java -jar sit-bt-core-0.9.jar -m Mode target
```

例 1) README_ja.md を日本語から英語に翻訳し README.md に出力するコマンド

```
java -jar sit-bt-core-0.9.jar -m ja2en README_ja.md:README.md
```

例 2) docs ディレクトリ以下の拡張子が adoc である全ファイルを日本語から英語に翻訳し、docs/en ディレクトリに出力するコマンド

```
java -jar sit-bt-core-0.9.jar -m ja2en -p *.adoc docs:docs/en
```

#### Maven Plugin として実行

pom.xml に Batch Traslator の Maven Plugin を追加します。

- pom.xml

```xml
<bulid>
  <plugins>
    <plugin>
        <groupId>io.sitoolkit.bt</groupId>
        <artifactId>sit-bt-maven-plugin</artifactId>
        <version>0.8</version>
    </plugin>
  </plugins>
</build>
```

```
mvn sit-bt:translate -Dmode=ja2en -Dtarget=README_ja.md:README.md
```

### バグ報告、機能要望

Batch Translator に関するバグや機能要望がある場合は当 GitHub プロジェクトの [Issues](https://github.com/sitoolkit/sit-bt/issues) に Issue を起票してください。

## ライセンス

Batch Translator は Apache Lisence 2.0 で公開しています。
