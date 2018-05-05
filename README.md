# ShinyLogger
This plugin logs the amount of shiny captures of a player in a CSV file.
The file can be found in the plugin's config directory (Usually `${server_root}/config/shinylogger/shinyCaptures.csv`)

## Installing the plugin on your server
To add the plugin to your server simply drop the jar file inside the mods folder and start the server.

## Usage
The plugin will automatically track the captured shiny Pokémon for each player online and store it in a file on the server.
To access the data while logged in to the server you can use the `/listshiny` _(Alias: `shinycaptures, showshinycaptures`)_ command. 
For this command to work you will need to have the `shinylogger.display` permission. _(Note: If there is no permissions plugin installed, you will need to be a server operator to use this command)_

The command has two ways to output the data. 

* The first one is the GUI which lists all the players.
Hoovering over a skull displays the player's name and the amount of captured shiny Pokémon registered by the plugin.

* The second one will is a text based list send to the player's chat or console. 
If the `/listshiny` command has been called through the console this will be the way the data is presented.
If you are logged into the server and would like this output in chat, simply run `/listshiny list` command. 
_(Note: If the list is too big it might clear your clients chat history, so be careful.)_


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
The full path of the Pixelmon jar will be: `lib\Pixelmon-1.12.2-6.2.3-universal`

### Create plugin jar
To create the jar, simply run `gradlew build`
