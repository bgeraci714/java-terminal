# java-terminal

This project is meant to mimic the Unix Terminal and a handful of its functions. 

Below is a full list of valid commands and their usage: 

#### Command:     ls

    Description: lists the files in the current directory

    Usage:   ls

#### Command:     cd

    Description: changes the current working directory. 

    Usage:   cd .. -> Moves you up one level. Most advised way to move through the directory structure.
             cd -> goes to the root directory, not advised to call tree here.
             cd full/valid/file/path -> moves to said file path
             cd name_of_an_alias -> moves straight to that alias if it's a valid directory

#### Command:     open

    Description: opens a file or website passed in as an argument. 
                 takes into account current working directory
    
    Usage:   open valid/file/path -> opens file with default program. 
             open name_of_an_alias -> opens file with default program.
             open -w prefix.website.[com/org/io/etc...] -> opens website

#### Command:     quit
    
    Description: exits the terminal and saves aliases. 
    
    Usage:
             quit 

#### Command:     grep
    
    Description: searches within a textfile for a given search query. 
    
    Usage:   grep "query" full_file_path

#### Command:     tree

    Description: pretty prints out the below file structure in a tree-like format. 
                 advised not to use at the root dir or other folders with a lot of (11,000+) 
                 folders/files unless time isn't a concern. do note that the project is still working. 
                 It just takes time to complete!
    
    Usage:   tree
    
    
#### Command:     find

    Description: searches all directory/file names below the current working directory for a search query. 

    Usage:   find search_query -> returns highlighted occurences of the search query

#### Command:     rm_alias
    
    Description: removes an alias passed as an argument. 
    
    Usage:   rm_alias alias_to_remove -> removes said alias

#### Command:     alias

    Description: creates a shortcut to a given file or directory path. 
                 Loads and saves a user's aliases at the start and end of a terminal session. 
    
    Usage:   alias name_of_an_alias -> saves an alias for the current working directory
             alias name_of_an_alias full/file/path -> saves an alias for the specified file path

#### Command:     manual

    Description: returns the manual
    
    Usage:   manual 

#### Command:     cwd

    Description: prints out the current working directory

    Usage:   cwd
