# ShinyLogger
This plugin logs the amount of shiny captures of a player in a CSV file.
The file can be found in the plugin's config directory (Usually `${server_root}/config/shinylogger/shinyCaptures.csv`)

## Building the project
### Manual dependencies
To build the project a Pixelmon jar must be present inside a folder called 'lib' at the root of the project. 
The specific naming of the jar can be found in the dependency section of the build.gradle file, appended with `.jar` .

For example: 
If the gradle file contains 
```
dependencies {
    compile name: 'Pixelmon-1.12.2-6.2.3-universal'
}
```
the full path of the Pixelmon jar will be: `lib\Pixelmon-1.12.2-6.2.3-universal`

### Create plugin jar
To create the jar, simply run `gradle build`
