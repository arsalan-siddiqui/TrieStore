package com.wirestorm.triestore;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Arsalan
 * @version 1.0
 * @since 14-Feb-16
 */
public class TrieStore {
    
    Map<String, Object[]> stores;
    private Object[] mChars = new Object[256];
    private Object mPrefixVal; // Used only for values of prefix keys.
    
    // Simple container for a string-value pair.
    private static class Leaf {
        
        public String mStr;
        public Object mVal;
        
        public Leaf(String str, Object val) {
            mStr = str;
            mVal = val;
        }
    }
    
    public TrieStore() {
        
        stores = new HashMap();
    }
    
    /**
     * This method is used to create store
     * @param storeName you need pass store name
     * @return status of your request
     */
    public String createStore(String storeName) {
        
        if (!"".equals(storeName) && storeName != null) {
            
            if (stores.get(storeName) == null) {
                
                stores.put(storeName, new Object[256]);
                return "Store " + storeName + " created";
            } else {
                
                return "Store " + storeName + " already exist";
            }
        } else {
            
            return "Store name can not be empty";
        }
    }
    
    /**
     * This method is used to delete store
     * @param storeName
     * @return
     */
    public String deleteStore(String storeName) {
        
        if (!"".equals(storeName) && storeName != null) {
            if (stores.get(storeName) != null) {
                stores.remove(storeName);
                return "Store " + storeName + " deleted";
            }else{
                
                return "Store does not exist";
            }
        }
        return "Unable to delete Store";
    }
    
    /**
     * This method checks the empty of prefix key
     * @return
     */
    public boolean isEmpty() {
        if (mPrefixVal != null) {
            return false;
        }
        for (Object o : mChars) {
            if (o != null) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * This method is used to store key/value pair into the Trie
     * @param storeName specify the store name you want to add node
     * @param key key for node
     * @param val value for node
     */
    public void addNode(String storeName, String key, Object val) {
        assert key != null;
        assert !(val instanceof TrieStore);
        if (key.length() == 0) {
            mPrefixVal = val; // Note: possibly removes or updates an item.
            return;
        }
        char c = key.charAt(0);
        mChars = stores.get(storeName);
        Object cObj = mChars[c];
        if (cObj == null) { // Unused slot means no collision so just store and return;
            if (val == null) {
                return; // Don't create a leaf to store a null value.
            }
            mChars[c] = new Leaf(key, val);
            return;
        }
        if (cObj instanceof TrieStore) {
            // Collided with an existing sub-branch so nibble a char and recurse.
            TrieStore childTrie = (TrieStore) cObj;
            childTrie.addNode(storeName, key.substring(1), val);
            if (val == null && childTrie.isEmpty()) {
                mChars[c] = null; // put() must have erased final entry so prune branch.
            }
            return;
        }
        // Collided with a leaf
        if (val == null) {
            mChars[c] = null; // Null value means to remove any previously stored value.
            return;
        }
        assert cObj instanceof Leaf;
        // Sprout a new branch to hold the colliding items.
        Leaf cLeaf = (Leaf) cObj;
        TrieStore branch = new TrieStore();
        branch.addNode(storeName, key.substring(1), val); // Store new value in new subtree.
        branch.addNode(storeName, cLeaf.mStr.substring(1), cLeaf.mVal); // Plus the one we collided with.
        mChars[c] = branch;
    }
    
    /**
     * This method is used to retrieve data form Trie
     * @param key to find
     * @param storeName in which store
     * @return value of that particular key
     */
    public Object get(String key, String storeName) {
        assert key != null;
        if (key.length() == 0) {
            return mPrefixVal;
        }
        char c = key.charAt(0);
        mChars = stores.get(storeName);
        Object cVal = mChars[c];
        if (cVal == null) {
            return null; // Not found.
        }
        assert cVal instanceof Leaf || cVal instanceof TrieStore;
        if (cVal instanceof TrieStore) { // Hash collision. Nibble first char, and recurse.
            return ((TrieStore) cVal).get(key.substring(1), storeName);
        }
        if (cVal instanceof Leaf) {
            Leaf cPair = (Leaf) cVal;
            if (key.equals(cPair.mStr)) {
                return cPair.mVal; // Return user's data value.
            }
        }
        return null; // Not found.
    }
    
    /**
     * This main method used to run app
     * @param args file name that contains commands
     * @throws ParseException exception in order to invalid parsing
     * @throws FileNotFoundException when file name or path is incorrect
     * @throws IOException incorrect input/output
     */
    public static void main(String[] args) throws ParseException, FileNotFoundException, IOException {
        
        TrieStore trieMap = new TrieStore();
        args = new String[]{"C:\\Users\\arslan siddiqui\\Downloads\\Compressed\\TrieStore\\commands.txt"};
        if(args.length == 0){
        
            System.out.println("Please pass the file name as a argument into the main method");
        }else{
        
            String output  = trieMap.executeCommands(args[0]);
            System.out.println("================================* Trie Store *======================================");
            System.out.println(output);
        }
    }
    
    /**
     * This method is used to executes commands
     * @param fileName contains commands to execute
     * @return output
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParseException 
     */
    public String executeCommands(String fileName) throws FileNotFoundException, IOException, ParseException{
    
        StringBuilder output = new StringBuilder();
        if(!"".equals(fileName) && fileName != null) {

            File file = new File(fileName);

            if (file.exists()) {
                InputStream inputStream = new FileInputStream(file);

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String format = "%-40s%s%n";
                output.append(String.format(format, "Command", "Output"));
                String line;
                while ((line = reader.readLine()) != null) {

                    if (!"".equals(line) && line != null) {

                        CommandLineParser commandParser = new BasicParser();

                        try {
                            CommandLine commandline = commandParser.parse(getOptions(), line.split("\\s+"), true);
                            output.append(String.format(format, line, validateCommands(commandline)));
                        } catch (MissingArgumentException e) {
                            output.append(String.format(format, line, e.getMessage()));
                        }
                    }
                }
                return output.toString();
            }
            return "File does not exist";
        }
        return "File name not be null or empty";
    }
    
    /**
     * This method is used to validate commands and create results
     * @param commandline command that need to execute
     * @return status of the given command
     */
    public String validateCommands(CommandLine commandline) {
        
        String[] args;
        if (commandline.hasOption("create")) {
            args = commandline.getOptionValues("create");
            if(args != null){
                if (args.length == 1) {
                    return createStore(args[0]);
                }
            }
            return "Invalie number of arguments";
        } else if (commandline.hasOption("delete")) {
            args = commandline.getOptionValues("delete");
            if(args != null){
                if (args.length == 1) {
                    return deleteStore(args[0]);
                }
            }
            return "Invalie number of arguments";
            
        } else if (commandline.hasOption("insert")) {
            
            args = commandline.getOptionValues("insert");
            if(args != null){
                if (args.length == 4) {
                    if("into".equalsIgnoreCase(args[2])){
                        if(stores.get(args[3]) == null){
                            
                            return "Store name does not exist";
                        }
                        addNode(args[3], args[0], args[1]);
                        return args[0] + " " + args[1] + " inserted into " + args[3];
                    }else{
                        
                        return "Invalid command(should be like: insert [key] [value] into [store)";
                    }
                }
            }
            return "Invalid number of arguments";
        } else if (commandline.hasOption("get")) {
            
            args = commandline.getOptionValues("get");
            if(args != null){
                if (args.length == 3) {
                    if("from".equalsIgnoreCase(args[1])){
                        if(stores.get(args[2]) == null){
                            
                            return "Store name does not exist";
                        }
                        if(get(args[0], args[2]) == null){
                            
                            return "Not found";
                        }else{
                            
                            return get(args[0], args[2]).toString();
                        }
                    }else{
                        
                        return "Invalid command(should be like: get [key] from [store]";
                    }
                }
            }
            return "Invalid number of arguments";
        }else if (commandline.hasOption("exists")) {
            args = commandline.getOptionValues("exists");
            if(args != null){
                if (args.length == 3) {
                    if("in".equalsIgnoreCase(args[1])){
                        if(stores.get(args[2]) == null){
                            
                            return "Store name does not exist";
                        }
                        
                        if(get(args[0], args[2]) != null){
                            
                            return "true";
                        }else{
                            
                            return "false";
                        }
                    }else{
                        
                        return "Invalid command(should be like: exists [key] in [store]";
                    }
                }
            }
            return "Invalid number of arguments";
        }else{
            
            return "Invalid command";
        }
    }
    
    /**
     * This method is used to create options for application
     * @return returns option list
     */
    public Options getOptions() {
        
        Option create = OptionBuilder.withArgName("storeName>")
                .withValueSeparator(' ')
                .hasArgs()
                .withLongOpt("create")
                .withDescription("This is used to create store")
                .create("create");
        
        Option delete = OptionBuilder.withArgName("storeName>")
                .withValueSeparator(' ')
                .hasArgs()
                .withLongOpt("delete")
                .withDescription("This is used to delete store")
                .create("delete");
        
        Option insert = OptionBuilder.withArgName("key> <value> <into> <storeName")
                .withValueSeparator(' ')
                .hasArgs()
                .withLongOpt("insert")
                .withDescription("This is used to insert data into the store")
                .create("insert");
        
        Option get = OptionBuilder.withArgName("key> <from> <storeName")
                .withValueSeparator(' ')
                .hasArgs()
                .withLongOpt("get")
                .withDescription("This is used for get data from store")
                .create("get");
        
        Option exists = OptionBuilder.withArgName("key> <from> <storeName")
                .withValueSeparator(' ')
                .hasArgs()
                .withLongOpt("exists")
                .withDescription("This is used check key exist into the store")
                .create("exists");
        
        Options options = new Options();
        options.addOption(create);
        options.addOption(delete);
        options.addOption(insert);
        options.addOption(get);
        options.addOption(exists);
        return options;
    }
}


