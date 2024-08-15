set JAVA_HOME=..\..\..\Java\openjdk-22.0.2
set JAVA_FX_HOME=..\..\..\JavaFX\javafx-sdk-22.0.2
set JAVA_FX_MODS=..\..\..\JavaFX\javafx-jmods-22.0.2
set WIX_HOME=..\..\..\WixToolSet\wix314-binaries
set Path=%Path%;%WIX_HOME%;%JAVA_HOME%\bin;%JAVA_FX_HOME%\lib

jpackage @option.txt --module-path %JAVA_FX_MODS%
