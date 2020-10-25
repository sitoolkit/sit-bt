[日本語](README_ja.md)

# Batch Translator

The Batch Translator is a CLI tool that translates text files.
The primary intended audience is OSS development project members who need to translate the README and documentation.
The main uses are :

- Translate 1 files (for example, README. md)
- Bulk translation of files with a specific extension under the directory (AsciiDoctor, for example, https://asciidoctor.org/) documents)

## Consumer

### Execution Environment

The following software is required to run the Batch Translator :

- Java 11+
- Maven 3.6 + (for use as a Maven Plugin)

#### Usage

Run the Batch Translator as a Java command or Maven Plugin.

1. Create API Key (#Create API Key)
1. Execute as Java Command or Maven Plugin

#### Creating an API Key

Batch Translator uses Minna no Hon' yaku @ TexTra ® as its translation engine. To use the translation feature, create an account at the following site :

https://mt-auto-minhon-mlt.ucri.jgn-x.jp/

Once the account is created, save the user ID, API KEY, and API SECRET in https://mt-auto-minhon-mlt.ucri.jgn-x.jp/content/setting/user/edit/) to the sit-at. properties file.

Create and store the sit-at. properties file in a directory named. sitoolkit under the user home directory.

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


#### Run with a Java command

```
curl -o sit-bt-0.8.jar https://repo1.maven.org/maven2/io/sitoolkit/bt/sit-bt-core-0.8.jar

java -jar sit-bt-0.8.jar -m Mode target
```

Example 1) Command to Translate README _ ja. md from Japanese to English and Output to README. md

```
java -jar sit-bt-0.8.jar -m ja2en "README_ja.md->README.md"
```


Example 2. docs directory Command to translate all files with adoc extension under the docs directory from Japanese to English and print them to the docs/en directory.

```
java -jar sit-bt-0.8.jar -m ja2en -p *.adoc "docs->docs/en"
```


#### Run as Maven Plugin

Add the Batch Traslator Maven plugin to pom. xml.

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
mvn sit-bt:translate -Dmode=ja2en -Dtarget=README_ja.md->README.md
```


### Bug Reports, Feature Requests

If you have any bugs or feature requests related to the Batch Translator, please refer to the GitHub project's Issues (Issue to https://github.com/sitoolkit/sit-bt/issues)).

## License

The Batch Translator is published in Apache Lisence 2.0.