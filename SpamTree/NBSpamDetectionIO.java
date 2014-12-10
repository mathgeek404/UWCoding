/*
	Spam detection using a Naive Bayes classifier.

	The program is incomplete, it only reads in messages
	and creates the dictionary together
	with the word counts for each class (spam and ham).
*/

import java.io.*;
import java.util.*;
import java.lang.*;

public class NBSpamDetectionIO
{
	// This a class with two counters (for ham and for spam)
	static class Multiple_Counter
	{
		int counterHam = 0;
		int counterSpam    = 0;
	}


	public static void main(String[] args)
	throws IOException
	{
		// Location of the directory (the path) taken from the cmd line (first arg)
		File dir_location      = new File( args[0] );
		
		// Listing of the directory (should contain 2 subdirectories: ham/ and spam/)
		File[] dir_listing     = new File[0];

		// Check if the cmd line arg is a directory and list it
		if ( dir_location.isDirectory() )
		{
			dir_listing = dir_location.listFiles();
		}
		else
		{
			System.out.println( "- Error: cmd line arg not a directory.\n" );
		        Runtime.getRuntime().exit(0);
		}
		
		// Listings of the two sub-directories (ham/ and spam/)
		File[] listing_ham = new File[0];
		File[] listing_spam    = new File[0];
		
		// Check that there are 2 sub-directories
		boolean hamFound = false; boolean spamFound = false;
		for (int i=0; i<dir_listing.length; i++) {
			if (dir_listing[i].getName().equals("ham")) { listing_ham = dir_listing[i].listFiles(); hamFound = true;}
			else if (dir_listing[i].getName().equals("spam")) { listing_spam = dir_listing[i].listFiles(); spamFound = true;}
		}
		if (!hamFound || !spamFound) {
			System.out.println( "- Error: specified directory does not contain ham and spam subdirectories.\n" );
		        Runtime.getRuntime().exit(0);
		}

		// Print out the number of messages in ham and in spam
		System.out.println( "\t number of ham messages is: " + listing_ham.length );
		System.out.println( "\t number of spam messages is: "    + listing_spam.length );
		
		// Create a hash table for the vocabulary (word searching is very fast in a hash table)
		Hashtable<String,Multiple_Counter> vocab = new Hashtable<String,Multiple_Counter>();
		Multiple_Counter old_cnt   = new Multiple_Counter();

		// Read the e-mail messages
		// The ham mail
		for ( int i = 0; i < listing_ham.length; i ++ )
		{
			FileInputStream i_s = new FileInputStream( listing_ham[i] );
			BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
			String line;
			String word;
			
        	        while ((line = in.readLine()) != null)					// read a line
			{
				StringTokenizer st = new StringTokenizer(line);			// parse it into words
		
				while (st.hasMoreTokens())
				{
					word = st.nextToken().replaceAll("[^a-zA-Z]","");
					
          if ( !word.equals("") ) { // if string isn't empty
						if ( vocab.containsKey(word) )				// check if word exists already in the vocabulary
						{
							old_cnt = vocab.get(word);	// get the counter from the hashtable
							old_cnt.counterHam ++;			// and increment it
					
							vocab.put(word, old_cnt);
						}
						else
						{
							Multiple_Counter fresh_cnt = new Multiple_Counter();
							fresh_cnt.counterHam = 1;
							fresh_cnt.counterSpam    = 0;
						
							vocab.put(word, fresh_cnt);			// put the new word with its new counter into the hashtable
						}
	        }
				}
			}

                	in.close();
		}
		// The spam mail
		for ( int i = 0; i < listing_spam.length; i ++ )
		{
			FileInputStream i_s = new FileInputStream( listing_spam[i] );
			BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
			String line;
			String word;
			
        	        while ((line = in.readLine()) != null)					// read a line
			{
				StringTokenizer st = new StringTokenizer(line);			// parse it into words
		
				while (st.hasMoreTokens())
				{
					word = st.nextToken().replaceAll("[^a-zA-Z]","");
					
				  if ( ! word.equals("") ) {	
						if ( vocab.containsKey(word) )				// check if word exists already in the vocabulary
						{
							old_cnt = vocab.get(word);	// get the counter from the hashtable
							old_cnt.counterSpam ++;			// and increment it
					
							vocab.put(word, old_cnt);
						}
						else
						{
							Multiple_Counter fresh_cnt = new Multiple_Counter();
							fresh_cnt.counterHam = 0;
							fresh_cnt.counterSpam    = 1;
						
							vocab.put(word, fresh_cnt);			// put the new word with its new counter into the hashtable
						}
					}
				}
			}

                	in.close();
		}
		
		// Print out the hash table
		for (Enumeration<String> e = vocab.keys() ; e.hasMoreElements() ;)
		{	
			String word;
			
			word = e.nextElement();
			old_cnt  = vocab.get(word);
			
			System.out.println( word + " | in ham: " + old_cnt.counterHam + 
			                             " in spam: "    + old_cnt.counterSpam);
		}
		
		// Now all students must continue from here
		// Prior probabilities must be computed from the number of ham and spam messages
		// Conditional probabilities must be computed for every unique word
    // add-1 smoothing must be implemented
		// Probabilities must be stored as log probabilities (log likelihoods).
		// Bayes rule must be applied on new messages, followed by argmax classification (using log probabilities)
		// Errors must be computed on the test set and a confusion matrix must be generated
	}
}
