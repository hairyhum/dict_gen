Generate dictionary of XML files from a set of XML files.

Takes a directory of XML files and creates two dictionaries with metadata
from source files.

This project is using maven.

How to run:

Build the package:

`mvn package`

Run from the command line:

`java -cp target/dict_gen-0.0.1-SNAPSHOT.jar  dict_gen.Generator ./data ./client.xml ./server.xml`

First arguent is the directory containing XML files, second is the "client format" dictionary file,
third is the "server format" dictionary file.

This project contains an example directory and some unit tests.

Source file format:

```
<DOCUMENT>
    <DOCUMENT_NAME>doc name</DOCUMENT_NAME>
    <VERSION>version</VERSION>
    <DATA>blahblah</DATA>
</DOCUMENT>
```


Destination file format (client):

```
<DICTIONARY>
    <DOCUMENT>
       <DOCUMENT_NAME>doc name</DOCUMENT_NAME>
        <VERSION>version</VERSION>
        <LENGTH>8</LENGTH>
    </DOCUMENT>
...
</DICTIONARY>
```


Destination file format (server):

```
<DICTIONARY>
    <DOCUMENT>
       <DOCUMENT_NAME>doc name</DOCUMENT_NAME>
        <VERSION>version</VERSION>
        <LENGTH>8</LENGTH>
        <PATH_TO_FILE>/file</PATH_TO_FILE>
    </DOCUMENT>
...
</DICTIONARY>
```
