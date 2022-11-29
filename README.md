[日本語](README_ja.md)

# Batch Translator

The Batch Translator is a CLI tool for translating text files.
The primary intended audience is OSS development project members who need to translate the README or documentation.
The main usage is as follows.

- Translation of a single file, such as README. md
- Bulk translation of files with a specific extension under the directory, such as AsciiDoctor (https://asciidoctor.org/) - based documents)

## Consumer

### Execution Environment

The following software is required to run the Batch Translator :

- Java 11+
- Maven 3.6 + for use as Maven Plugin

#### Usage

Run the Batch Translator as a Java command or as a Maven Plugin.

1. Create API Key (Create #API Key)
1. Run as a Java Command or as a Maven Plugin

#### Creating an API Key

The Batch Translator uses the following translation engines.
* Minna no Hon yaku @ TexTra ®
* Amazon Translate
* Azure Translate

 To use the translation function, create an account on one of the following sites.

##### When using "Minna no Hon' yaku @ TexTra ®"

https://mt-auto-minhon-mlt.ucri.jgn-x.jp/

After you create the account, click the Settings Page (in https://mt-auto-minhon-mlt.ucri.jgn-x.jp/content/setting/user/edit/), save the user ID, API KEY, and API SECRET in the sit-at. properties file.

Create a directory named. sitoolkit under the user home directory and save the sit-at. properties file there.

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



##### When using "Amazon Translate"

https://portal.aws.amazon.com/billing/signup#/start

After creating an account, refer to the [User Guide](https://docs.aws.amazon.com/ja_jp/cli/latest/userguide/cli-configure-files.html) and save aws_access_key_id and aws_secret_access_key in the credentials file.

Create a credentials file under the .aws folder in your home directory.

Ex. ~/.aws/credentials
```properties
[default]
aws_access_key_id=AKIAIOSFODNN7EXAMPLE
aws_secret_access_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
```



###### When using "Azure Translate"

retouching





#### Run with Java commands

```
curl -o sit-bt-core-0.9.jar https://repo1.maven.org/maven2/io/sitoolkit/bt/sit-bt-core/0.9/sit-bt-core-0.9.jar

java -jar sit-bt-core-0.9.jar -m Mode target
```

Example 1) Command to translate README _ ja. md from Japanese to English and to output to README. md

```
java -jar sit-bt-core-0.9.jar -m ja2en README_ja.md:README.md
```


Example 2) Command to translate all files with adoc extension under docs directory from Japanese to English and output to docs/en directory

```
java -jar sit-bt-core-0.9.jar -m ja2en -p *.adoc docs:docs/en
```


#### Run as Maven Plugin

Add the Batch Traslator Maven Plugin to pom. xml.

- pom.xml

```xml
<bulid>
  <plugins>
    <plugin>
        <groupId>io.sitoolkit.bt</groupId>
        <artifactId>sit-bt-maven-plugin</artifactId>
        <version>0.9</version>
    </plugin>
  </plugins>
</build>
```


```
mvn sit-bt:translate -Dmode=ja2en -Dtarget=README_ja.md:README.md
```


### bug reports, feature requests

If you have a bug or feature request related to the Batch Translator, please issue an issue to https://github.com/sitoolkit/sit-bt/issues).

## license

The Batch Translator is exposed at Apache Lisence 2.0.