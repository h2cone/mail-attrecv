![Java CI with Maven](https://github.com/h2cone/mail-attrecv/workflows/Java%20CI%20with%20Maven/badge.svg)

# mail-attrecv

Download email attachments automatically.

## Requirements

- Java 8 runtime environments

- IMAP Server

## Install

Download from [here](https://github.com/h2cone/mail-attrecv/releases/).

## Configuration

By default, the process reads the `app.properties` in the current directory.

```properties
# app setting
mail.protocol.username=username
mail.protocol.password=password
# filter by author
mail.search.term.from=tw8ape@gmail.com
# filter by subject
mail.search.term.subject=测试
# download location
mail.download.attachment.saveDir=attachments
mail.idle.test.initialDelay=0
mail.idle.test.period=5000
# server setting
mail.imap.host=imap.exmail.qq.com
mail.imap.port=993
# ssl setting
mail.imap.socketFactory.port=993
mail.imap.socketFactory.fallback=false
mail.imap.socketFactory.class=javax.net.ssl.SSLSocketFactory
```

## Startup

```shell
java -jar mail-attrecv.jar
```

or

```shell
java -jar mail-attrecv.jar -c=<Path to properties file>
```
