package com.wirestorm.triestore.test;

import com.wirestorm.triestore.TrieStore;
import java.io.*;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class TrieStoreTest {
    
    TrieStore trieStore = new TrieStore();
    
    
    @Test
    public void testExecuteCommandsPass() throws IOException, FileNotFoundException, ParseException{
      Assert.assertEquals("error in execute commands method", IOUtils.toString(TrieStoreTest.class.getClassLoader().getResourceAsStream("output.txt")).trim(), trieStore.executeCommands("commands.txt").trim());
    }
}