package CommandTable;

public class Colors {
    public static String getTypeColor(String fileName){
        final String ANSI_BLACK = "\u001B[30m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_BLUE = "\u001B[34m";
        final String ANSI_PURPLE = "\u001B[35m";
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_WHITE = "\u001B[37m";
        
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1)
            return ANSI_BLACK;
        
        String type = fileName.substring(dotIndex);
        
        switch(type){
            case ".jpg":
            case ".png":
                return ANSI_YELLOW;
            case ".doc":
            case ".docx":
            case ".txt":
            case ".rtf":
                return ANSI_BLUE;
            case ".pdf":
                return ANSI_RED;
            case ".xls":
            case ".xlsx":
                return ANSI_GREEN;
            default:
                return ANSI_BLACK;
        }        
   
       
    }
    
    public static String highlight(String full, String block)
    {
        return highlight(full,block,"Purple");
    }
    
    public static String highlight(String full, String block, String color)
    {       
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_BLACK = "\u001B[30m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_BLUE = "\u001B[34m";
        final String ANSI_PURPLE = "\u001B[35m";
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_WHITE = "\u001B[37m";
        
        switch(color.toUpperCase())
        {
            case "BLACK":
                color = ANSI_BLACK;
                break;
            case "RED":
                color = ANSI_RED;
                break;
            case "GREEN":
                color = ANSI_GREEN;
                break;
            case "YELLOW":
                color = ANSI_YELLOW;
                break;
            case "BLUE":
                color = ANSI_BLUE;
                break;
            case "PURPLE":
                color = ANSI_PURPLE;
                break;
            case "CYAN":
                color = ANSI_CYAN;
                break;
            case "WHITE":
                color = ANSI_WHITE;
                break;
            default:
                color = ANSI_YELLOW;
                break;                
        }
        
        StringBuilder sb = new StringBuilder();
        int start = full.toLowerCase().indexOf(block.toLowerCase());
        
        sb.append(full, 0, start);
        sb.append(color);
        sb.append(full, start, start + block.length());
        sb.append(ANSI_RESET);
        sb.append(full, start + block.length(), full.length());
        
        return sb.toString();
                
    }
}
