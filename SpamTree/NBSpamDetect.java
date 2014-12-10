/*NOTES
 * STATUS:
 * Finished first 2 parts of program.
 * 
 * *** The program's dir_listing is alphabetically wrong; thus, the spam_listing and 
 * ham_listing are mixed up. Emailed to see what to do. For now, results will be swapped (ham is spam, etc)
 * 
 * TODO:
 * Check ADD 1 on P(SPAM), P(HAM), on the words as well
 * Check probabilities
 * 
 * CHECKED PARTS: 
 * nWordsSpam and nWordsHam are counted correctly 
 * Correct accuracy on first 2 cases
 * 
 */


// Now all students must continue from here
// Prior probabilities must be computed from the number of ham and spam messages
// Conditional probabilities must be computed for every unique word
// add-1 smoothing must be implemented
// Probabilities must be stored as log probabilities (log likelihoods).
// Bayes rule must be applied on new messages, followed by argmax classification (using log probabilities)
// Errors must be computed on the test set and a confusion matrix must be generated

/*
	Spam detection using a Naive Bayes classifier.

	The program is incomplete, it only reads in messages
	and creates the dictionary together
	with the word counts for each class (spam and ham).
 */

import java.io.*;
import java.util.*;
import java.lang.*;


public class NBSpamDetect {
   // This a class with two counters (for ham and for spam)


   public static void main(String[] args) throws IOException {
      mainClassifier(args);
      ignoreCaseClassifier(args);
      limitedClassifier(args);
   }



   public static void mainClassifier(String[] args) throws IOException {
      Double nWordsSpam = 0.0;
      Double nWordsHam = 0.0;

      Hashtable<String,Double> spamTable;
      Hashtable<String,Double> hamTable;

      Hashtable<String,Multiple_Counter> trainTable = new Hashtable<String,Multiple_Counter>();
      Multiple_Counter old_cnt   = new Multiple_Counter();


      /*
       * 
       * Parse the data
       * 
       */
      File dir_location      = new File(args[0]);
      File[] dir_listing     = new File[0];
      if ( dir_location.isDirectory() ) { dir_listing = dir_location.listFiles();}
      else {
         System.out.println( "- Error: cmd line arg not a directory.\n" );
         Runtime.getRuntime().exit(0);
      }
      File[] listing_ham = new File[0]; File[] listing_spam = new File[0];
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



      /*
       * Priors Calculations
       */
      double nMessagesHam = (double)listing_ham.length;
      double nMessagesSpam = (double)listing_spam.length;
      double nMessagesTotal = nMessagesHam+nMessagesSpam;

      final double probSpam = Math.log(nMessagesSpam/(nMessagesTotal));
      final double probHam = Math.log(nMessagesHam/(nMessagesTotal));



      // Read the e-mail messages
      // The ham mail
      for ( int i = 0; i < listing_ham.length; i ++ )
      {
         FileInputStream i_s = new FileInputStream( listing_ham[i] );
         BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
         String line;
         String word;

         while ((line = in.readLine()) != null)             // read a line
         {
            StringTokenizer st = new StringTokenizer(line);       // parse it into words

            while (st.hasMoreTokens())
            {
               word = st.nextToken().replaceAll("[^a-zA-Z]","");

               if ( !word.equals("") ) { // if string isn't empty
                  nWordsHam++;
                  if ( trainTable.containsKey(word) )            // check if word exists already in the vocabulary
                  {
                     old_cnt = trainTable.get(word); // get the counter from the hashtable
                     old_cnt.counterHam ++;        // and increment it

                     trainTable.put(word, old_cnt);
                  }
                  else
                  {
                     Multiple_Counter fresh_cnt = new Multiple_Counter();
                     fresh_cnt.counterHam = 1;
                     fresh_cnt.counterSpam    = 0;

                     trainTable.put(word, fresh_cnt);         // put the new word with its new counter into the hashtable
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

         while ((line = in.readLine()) != null)             // read a line
         {
            StringTokenizer st = new StringTokenizer(line);       // parse it into words

            while (st.hasMoreTokens())
            {
               word = st.nextToken().replaceAll("[^a-zA-Z]","");

               if ( ! word.equals("") ) { 
                  nWordsSpam++;
                  if ( trainTable.containsKey(word) )            // check if word exists already in the vocabulary
                  {
                     old_cnt = trainTable.get(word); // get the counter from the hashtable
                     old_cnt.counterSpam ++;       // and increment it

                     trainTable.put(word, old_cnt);
                  }
                  else
                  {
                     Multiple_Counter fresh_cnt = new Multiple_Counter();
                     fresh_cnt.counterHam = 0;
                     fresh_cnt.counterSpam    = 1;

                     trainTable.put(word, fresh_cnt);         // put the new word with its new counter into the hashtable
                  }
               }
            }
         }
         in.close();
      }


      /*
       * 
       * CALCULATE PROBABILITIES
       * 
       */

      spamTable = new Hashtable<String, Double>();
      hamTable = new Hashtable<String, Double>();
      Double vocabSize = (double)trainTable.keySet().size();

      for (String key: trainTable.keySet()) {
         Double condHam = (trainTable.get(key).counterHam+1)/(nWordsHam+vocabSize);
         condHam = Math.log(condHam);
         hamTable.put(key, condHam);

         Double condSpam = (trainTable.get(key).counterSpam+1)/(nWordsSpam+vocabSize);
         condSpam = Math.log(condSpam);
         spamTable.put(key, condSpam);
      }

      /*
       * 
       * TESTING STAGES
       * 
       */

      dir_location = new File(args[1]);
      dir_listing = new File[0];
      if (dir_location.isDirectory()){ dir_listing = dir_location.listFiles();}
      else {
         System.out.println( "- Error: cmd line arg not a directory.\n" );
         Runtime.getRuntime().exit(0);
      }
      // Listings of the two subdirectories (ham/ and spam/)
      listing_ham = new File[0];
      listing_spam    = new File[0];

   // Check that there are 2 sub-directories
      hamFound = false; spamFound = false;
      for (int i=0; i<dir_listing.length; i++) {
         if (dir_listing[i].getName().equals("ham")) { listing_ham = dir_listing[i].listFiles(); hamFound = true;}
         else if (dir_listing[i].getName().equals("spam")) { listing_spam = dir_listing[i].listFiles(); spamFound = true;}
      }
      if (!hamFound || !spamFound) {
         System.out.println( "- Error: specified directory does not contain ham and spam subdirectories.\n" );
              Runtime.getRuntime().exit(0);
      }

      // Read the e-mail messages
      // The ham mail

      int trueHam_classHam = 0;
      int trueHam_classSpam = 0;


      for ( int i = 0; i < listing_ham.length; i ++ )
      {
         FileInputStream i_s = new FileInputStream( listing_ham[i] );
         BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
         String line;
         String word;
         Double msgHamProb = probHam;
         Double msgSpamProb = probSpam;

         while ((line = in.readLine()) != null)             // read a line
         {
            StringTokenizer st = new StringTokenizer(line);       // parse it into words

            while (st.hasMoreTokens())
            {
               word = st.nextToken().replaceAll("[^a-zA-Z]","");

               if ( !word.equals("") ) { // if string isn't empty
                  if (trainTable.containsKey(word) ) {
                     msgHamProb += hamTable.get(word);
                     msgSpamProb += spamTable.get(word);
                  }
               }
            }
         }

         in.close();

         if (msgSpamProb > msgHamProb) {
            trueHam_classSpam++;  
         }
         else {
            trueHam_classHam++;
         }
      }
      // The spam mail

      int trueSpam_classSpam = 0;
      int trueSpam_classHam = 0;
      for ( int i = 0; i < listing_spam.length; i ++ )
      {
         FileInputStream i_s = new FileInputStream(listing_spam[i]);
         BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
         String line;
         String word;
         Double msgHamProb = probHam;
         Double msgSpamProb = probSpam;


         while ((line = in.readLine()) != null)             // read a line
         {
            StringTokenizer st = new StringTokenizer(line);       // parse it into words

            while (st.hasMoreTokens())
            {
               word = st.nextToken().replaceAll("[^a-zA-Z]","");



               if ( ! word.equals("") ) { 
                  if ( trainTable.containsKey(word) )            // check if word exists already in the vocabulary
                  {
                     msgHamProb += hamTable.get(word);
                     msgSpamProb += spamTable.get(word);
                  }

               }
            }
         }
         in.close();


         if (msgSpamProb > msgHamProb) {
            trueSpam_classSpam++;  
         }
         else {
            trueSpam_classHam++;
         }
      }

      System.out.println("Original Case Results:");
      System.out.println("True Positive:" + trueSpam_classSpam);
      System.out.println("True Negative:" + trueHam_classHam);
      System.out.println("False Positive:" + trueHam_classSpam);
      System.out.println("False Negative:" + trueSpam_classHam); 
   }



   public static void ignoreCaseClassifier(String[] args) throws IOException{

      Double nWordsSpam = 0.0;
      Double nWordsHam = 0.0;
      Hashtable<String,Double> spamTable;
      Hashtable<String,Double> hamTable;


      /*
       * 
       * Parse the data
       * 
       */
      File dir_location      = new File(args[0]);
      File[] dir_listing     = new File[0];
      if ( dir_location.isDirectory() ) { dir_listing = dir_location.listFiles();}
      else {
         System.out.println( "- Error: cmd line arg not a directory.\n" );
         Runtime.getRuntime().exit(0);
      }
      File[] listing_ham = new File[0]; File[] listing_spam = new File[0];

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

      /*
       * Priors Calculations
       */
      double nMessagesHam = (double)listing_ham.length;
      double nMessagesSpam = (double)listing_spam.length;
      double nMessagesTotal = nMessagesHam+nMessagesSpam;

      final double probSpam = Math.log(nMessagesSpam/(nMessagesTotal));
      final double probHam = Math.log(nMessagesHam/(nMessagesTotal));


      // Create a hash table for the vocabulary (word searching is very fast in a hash table)
      Hashtable<String,Multiple_Counter> trainTable = new Hashtable<String,Multiple_Counter>();
      Multiple_Counter old_cnt   = new Multiple_Counter();

      // Read the e-mail messages
      // The ham mail
      for ( int i = 0; i < listing_ham.length; i ++ )
      {
         FileInputStream i_s = new FileInputStream( listing_ham[i] );
         BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
         String line;
         String word;

         while ((line = in.readLine()) != null)             // read a line
         {
            StringTokenizer st = new StringTokenizer(line);       // parse it into words

            while (st.hasMoreTokens())
            {
               word = st.nextToken().replaceAll("[^a-zA-Z]","");

               if ( !word.equals("") ) { // if string isn't empty
                  nWordsHam++;
                  word = word.toLowerCase();
                  if ( trainTable.containsKey(word) )            // check if word exists already in the vocabulary
                  {
                     old_cnt = trainTable.get(word); // get the counter from the hashtable
                     old_cnt.counterHam ++;        // and increment it

                     trainTable.put(word, old_cnt);
                  }
                  else
                  {
                     Multiple_Counter fresh_cnt = new Multiple_Counter();
                     fresh_cnt.counterHam = 1;
                     fresh_cnt.counterSpam = 0;

                     trainTable.put(word, fresh_cnt);         // put the new word with its new counter into the hashtable
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

         while ((line = in.readLine()) != null)             // read a line
         {
            StringTokenizer st = new StringTokenizer(line);       // parse it into words

            while (st.hasMoreTokens())
            {
               word = st.nextToken().replaceAll("[^a-zA-Z]","");

               if ( ! word.equals("") ) { 
                  nWordsSpam++;
                  word = word.toLowerCase();
                  if ( trainTable.containsKey(word) )            // check if word exists already in the vocabulary
                  {
                     old_cnt = trainTable.get(word); // get the counter from the hashtable
                     old_cnt.counterSpam ++;       // and increment it

                     trainTable.put(word, old_cnt);
                  }
                  else
                  {
                     Multiple_Counter fresh_cnt = new Multiple_Counter();
                     fresh_cnt.counterHam = 0;
                     fresh_cnt.counterSpam    = 1;

                     trainTable.put(word, fresh_cnt);         // put the new word with its new counter into the hashtable
                  }
               }
            }
         }
         in.close();
      }


      /*
       * 
       * CALCULATE PROBABILITIES
       * 
       */
      spamTable = new Hashtable<String, Double>();
      hamTable = new Hashtable<String, Double>();
      Double vocabSize = (double)trainTable.keySet().size();

      for (String key: trainTable.keySet()) {
         Double condHam = (trainTable.get(key).counterHam+1)/(nWordsHam+vocabSize);
         condHam = Math.log(condHam);
         hamTable.put(key, condHam);

         Double condSpam = (trainTable.get(key).counterSpam+1)/(nWordsSpam+vocabSize);
         condSpam = Math.log(condSpam);
         spamTable.put(key, condSpam);
      }

      /*
       * 
       * TESTING STAGES
       * 
       */

      dir_location = new File(args[1]);
      dir_listing = new File[0];
      if (dir_location.isDirectory()){ dir_listing = dir_location.listFiles();}
      else {
         System.out.println( "- Error: cmd line arg not a directory.\n" );
         Runtime.getRuntime().exit(0);
      }
      // Listings of the two subdirectories (ham/ and spam/)
      listing_ham = new File[0];
      listing_spam    = new File[0];

   // Check that there are 2 sub-directories
       hamFound = false; spamFound = false;
      for (int i=0; i<dir_listing.length; i++) {
         if (dir_listing[i].getName().equals("ham")) { listing_ham = dir_listing[i].listFiles(); hamFound = true;}
         else if (dir_listing[i].getName().equals("spam")) { listing_spam = dir_listing[i].listFiles(); spamFound = true;}
      }
      if (!hamFound || !spamFound) {
         System.out.println( "- Error: specified directory does not contain ham and spam subdirectories.\n" );
              Runtime.getRuntime().exit(0);
      }

      // Read the e-mail messages
      // The ham mail

      int trueHam_classHam = 0;
      int trueHam_classSpam = 0;
      int trueSpam_classSpam = 0;
      int trueSpam_classHam = 0;

      for ( int i = 0; i < listing_ham.length; i ++ )
      {
         FileInputStream i_s = new FileInputStream( listing_ham[i] );
         BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
         String line;
         String word;
         Double msgHamProb = probHam;
         Double msgSpamProb = probSpam;

         while ((line = in.readLine()) != null)             // read a line
         {
            StringTokenizer st = new StringTokenizer(line);       // parse it into words

            while (st.hasMoreTokens())
            {
               word = st.nextToken().replaceAll("[^a-zA-Z]","");

               if ( !word.equals("") ) { // if string isn't empty
                  word=word.toLowerCase();
                  if (trainTable.containsKey(word) ) {
                     msgHamProb += hamTable.get(word);
                     msgSpamProb += spamTable.get(word);
                  }
               }
            }
         }
         in.close();

         if (msgSpamProb > msgHamProb) {
            trueHam_classSpam++;  
         }
         else {
            trueHam_classHam++;
         }
      }
      // The spam mail

      for ( int i = 0; i < listing_spam.length; i ++ )
      {
         FileInputStream i_s = new FileInputStream( listing_spam[i] );
         BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
         String line;
         String word;
         Double msgHamProb = probHam;
         Double msgSpamProb = probSpam;

         while ((line = in.readLine()) != null)             // read a line
         {
            StringTokenizer st = new StringTokenizer(line);       // parse it into words

            while (st.hasMoreTokens())
            {
               word = st.nextToken().replaceAll("[^a-zA-Z]","");

               if ( ! word.equals("") ) { 
                  word=word.toLowerCase();
                  if ( trainTable.containsKey(word) )            
                  {
                     msgHamProb += hamTable.get(word);
                     msgSpamProb += spamTable.get(word);
                  }

               }
            }
         }
         in.close();


         if (msgSpamProb > msgHamProb) {
            trueSpam_classSpam++;  
         }
         else {
            trueSpam_classHam++;
         }
      }

      System.out.println();
      System.out.println("Ignore Case Results:");
      System.out.println("True Positive:" + trueSpam_classSpam);
      System.out.println("True Negative:" + trueHam_classHam);
      System.out.println("False Positive:" + trueHam_classSpam);
      System.out.println("False Negative:" + trueSpam_classHam); 

   }



   public static void limitedClassifier(String[] args) throws IOException {

      Set<String> fields = new HashSet<String>();
      fields.add("To:");fields.add("From:");fields.add("Subject:");fields.add("Cc:");

      Double nWordsSpam = 0.0;
      Double nWordsHam = 0.0;

      Hashtable<String,Double> spamTable;
      Hashtable<String,Double> hamTable;

      Hashtable<String,Multiple_Counter> trainTable = new Hashtable<String,Multiple_Counter>();
      Multiple_Counter old_cnt   = new Multiple_Counter();


      /*
       * 
       * Parse the data
       * 
       */
      File dir_location      = new File(args[0]);
      File[] dir_listing     = new File[0];
      if ( dir_location.isDirectory() ) { dir_listing = dir_location.listFiles();}
      else {
         System.out.println( "- Error: cmd line arg not a directory.\n" );
         Runtime.getRuntime().exit(0);
      }
      File[] listing_ham = new File[0]; File[] listing_spam = new File[0];
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



      /*
       * Priors Calculations
       */
      double nMessagesHam = (double)listing_ham.length;
      double nMessagesSpam = (double)listing_spam.length;
      double nMessagesTotal = nMessagesHam+nMessagesSpam;

      final double probSpam = Math.log(nMessagesSpam/(nMessagesTotal));
      final double probHam = Math.log(nMessagesHam/(nMessagesTotal));



      // Read the e-mail messages
      // The ham mail
      for ( int i = 0; i < listing_ham.length; i ++ )
      {
         FileInputStream i_s = new FileInputStream( listing_ham[i] );
         BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
         String line;
         String word;

         while ((line = in.readLine()) != null)             // read a line
         {
            StringTokenizer st = new StringTokenizer(line);       // parse it into words
            if (st.hasMoreTokens()) {
               if (!fields.contains(st.nextToken())) {
                  continue;
               }
            }

            while (st.hasMoreTokens())
            {
               word = st.nextToken().replaceAll("[^a-zA-Z]","");

               if ( !word.equals("") ) { // if string isn't empty
                  nWordsHam++;
                  if ( trainTable.containsKey(word) )            // check if word exists already in the vocabulary
                  {
                     old_cnt = trainTable.get(word); // get the counter from the hashtable
                     old_cnt.counterHam ++;        // and increment it

                     trainTable.put(word, old_cnt);
                  }
                  else
                  {
                     Multiple_Counter fresh_cnt = new Multiple_Counter();
                     fresh_cnt.counterHam = 1;
                     fresh_cnt.counterSpam    = 0;

                     trainTable.put(word, fresh_cnt);         // put the new word with its new counter into the hashtable
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

         while ((line = in.readLine()) != null)             // read a line
         {
            StringTokenizer st = new StringTokenizer(line);       // parse it into words
            if (st.hasMoreTokens()) {
               if (!fields.contains(st.nextToken())) {
                  continue;
               }
            }
            while (st.hasMoreTokens())
            {
               word = st.nextToken().replaceAll("[^a-zA-Z]","");


               if ( ! word.equals("") ) { 
                  nWordsSpam++;
                  if ( trainTable.containsKey(word) )            // check if word exists already in the vocabulary
                  {
                     old_cnt = trainTable.get(word); // get the counter from the hashtable
                     old_cnt.counterSpam ++;       // and increment it

                     trainTable.put(word, old_cnt);
                  }
                  else
                  {
                     Multiple_Counter fresh_cnt = new Multiple_Counter();
                     fresh_cnt.counterHam = 0;
                     fresh_cnt.counterSpam    = 1;

                     trainTable.put(word, fresh_cnt);         // put the new word with its new counter into the hashtable
                  }
               }
            }
         }
         in.close();
      }


      /*
       * 
       * CALCULATE PROBABILITIES
       * 
       */

      spamTable = new Hashtable<String, Double>();
      hamTable = new Hashtable<String, Double>();
      Double vocabSize = (double)trainTable.keySet().size();

      for (String key: trainTable.keySet()) {
         Double condHam = (trainTable.get(key).counterHam+1)/(nWordsHam+vocabSize);
         condHam = Math.log(condHam);
         hamTable.put(key, condHam);

         Double condSpam = (trainTable.get(key).counterSpam+1)/(nWordsSpam+vocabSize);
         condSpam = Math.log(condSpam);
         spamTable.put(key, condSpam);
      }

      /*
       * 
       * TESTING STAGES
       * 
       */

      dir_location = new File(args[1]);
      dir_listing = new File[0];
      if (dir_location.isDirectory()){ dir_listing = dir_location.listFiles();}
      else {
         System.out.println( "- Error: cmd line arg not a directory.\n" );
         Runtime.getRuntime().exit(0);
      }
      // Listings of the two subdirectories (ham/ and spam/)
      listing_ham = new File[0];
      listing_spam    = new File[0];

   // Check that there are 2 sub-directories
      hamFound = false; spamFound = false;
      for (int i=0; i<dir_listing.length; i++) {
         if (dir_listing[i].getName().equals("ham")) { listing_ham = dir_listing[i].listFiles(); hamFound = true;}
         else if (dir_listing[i].getName().equals("spam")) { listing_spam = dir_listing[i].listFiles(); spamFound = true;}
      }
      if (!hamFound || !spamFound) {
         System.out.println( "- Error: specified directory does not contain ham and spam subdirectories.\n" );
              Runtime.getRuntime().exit(0);
      }

      // Read the e-mail messages
      // The ham mail

      int trueHam_classHam = 0;
      int trueHam_classSpam = 0;


      for ( int i = 0; i < listing_ham.length; i ++ )
      {
         FileInputStream i_s = new FileInputStream( listing_ham[i] );
         BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
         String line;
         String word;
         Double msgHamProb = probHam;
         Double msgSpamProb = probSpam;

         while ((line = in.readLine()) != null)             // read a line
         {
            StringTokenizer st = new StringTokenizer(line);       // parse it into words
            if (st.hasMoreTokens()) {
               if (!fields.contains(st.nextToken())) {
                  continue;
               }
            }
            while (st.hasMoreTokens())
            {
               word = st.nextToken().replaceAll("[^a-zA-Z]","");

               if ( !word.equals("") ) { // if string isn't empty
                  if (trainTable.containsKey(word) ) {
                     msgHamProb += hamTable.get(word);
                     msgSpamProb += spamTable.get(word);
                  }
               }
            }
         }

         in.close();

         if (msgSpamProb > msgHamProb) {
            trueHam_classSpam++;  
         }
         else {
            trueHam_classHam++;
         }
      }
      // The spam mail

      int trueSpam_classSpam = 0;
      int trueSpam_classHam = 0;
      for ( int i = 0; i < listing_spam.length; i ++ )
      {
         FileInputStream i_s = new FileInputStream(listing_spam[i]);
         BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
         String line;
         String word;
         Double msgHamProb = probHam;
         Double msgSpamProb = probSpam;


         while ((line = in.readLine()) != null)             // read a line
         {
            StringTokenizer st = new StringTokenizer(line);       // parse it into words
            if (st.hasMoreTokens()) {
               if (!fields.contains(st.nextToken())) {
                  continue;
               }
            }
            while (st.hasMoreTokens())
            {
               word = st.nextToken().replaceAll("[^a-zA-Z]","");



               if ( ! word.equals("") ) { 
                  if ( trainTable.containsKey(word) )            // check if word exists already in the vocabulary
                  {
                     msgHamProb += hamTable.get(word);
                     msgSpamProb += spamTable.get(word);
                  }

               }
            }
         }
         in.close();


         if (msgSpamProb > msgHamProb) {
            trueSpam_classSpam++;  
         }
         else {
            trueSpam_classHam++;
         }
      }
      
      System.out.println();
      System.out.println("Limited Fields Results:");
      System.out.println("True Positive:" + trueSpam_classSpam);
      System.out.println("True Negative:" + trueHam_classHam);
      System.out.println("False Positive:" + trueHam_classSpam);
      System.out.println("False Negative:" + trueSpam_classHam); 

   }

}

class Multiple_Counter {
   int counterHam = 0;
   int counterSpam    = 0;
}
