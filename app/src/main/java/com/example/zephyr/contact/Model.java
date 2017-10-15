package com.example.zephyr.contact;

import android.content.Context;
import android.util.Log;

import com.cloudant.sync.documentstore.DocumentBodyFactory;
import com.cloudant.sync.documentstore.DocumentNotFoundException;
import com.cloudant.sync.documentstore.DocumentRevision;
import com.cloudant.sync.documentstore.DocumentStore;
import com.cloudant.sync.documentstore.DocumentStoreException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zephyr on 10/12/2017.
 */

public class Model {
    private static final String LOG_TAG = "Model";

    private DocumentRevision rev,revision,retrieved,updated;
    private DocumentStore ds,ds2;
    Map<String, Object> json;

    File path;
    Model(Context context)  {


            path = context.getApplicationContext().getDir(
                    "tmper",
                    Context.MODE_PRIVATE);
    }
    // on Android, we could do something like:
    //File path = getApplicationContext().getDir("tmp",1);

    // Once you've got a path, it's straightforward to create DocumentStores:
     void documentCreation(){
        try {
            // Once you've got a path, it's straightforward to create DocumentStores:
            ds = DocumentStore.getInstance(new File(path, "my_document_store"));
            ds2 = DocumentStore.getInstance(new File(path, "other_document_store"));
            createDocRevision();
        }catch (Exception exception){
            Log.e(LOG_TAG, "Unable to open DocumentStore", exception);
        }
    }

    private  void createDocRevision(){
        //rev = new DocumentRevision("doc1");
        // Or don't assign the docId property, we'll generate one
        rev = new DocumentRevision();
    }
    public   void buildingUpBody(String name,String contact){
        documentCreation();
        Person person=new Person(name,contact);

        // Build up body content from a Map
        json = new HashMap<String, Object>();
        json.put("Name", name);
        json.put("Contact", contact);
        rev.setBody(DocumentBodyFactory.create(json));
        createDatabase();
    }

      public void insert(String name,String contact){
          buildingUpBody(name,contact);
      }
    private void createDatabase()  {
         // Now call create(). Note that we call all of the CRUD methods through the Database
         // instance which is obtained via the database() getter.
         try{
              revision = ds.database().create(rev);
         } catch (Exception e) {
             Log.e(LOG_TAG, "Unable to create DocumentStore", e);
             e.printStackTrace();
         }

    }

     private void  retrieveDocument(){
         try {
             String docId = revision.getId();
             retrieved=ds.database().read(docId);
         } catch (DocumentNotFoundException e) {
             e.printStackTrace();
         } catch (DocumentStoreException e) {
             e.printStackTrace();
         }
     }

     private void retrieveAsJson(){
         retrieveDocument();
         // This document is mutable and you can make changes to it, as shown below.
         // To update a document, make your changes and save the document:
         json = retrieved.getBody().asMap();


     }

     public void readDocument(String key){
         retrieveAsJson();
         System.out.println("haah "+json.get(key).toString());
     }

     public void update() {
         retrieveAsJson();
         json.put("address", "islamabad");
         retrieved.setBody(DocumentBodyFactory.create(json));
         // Note that "updated" is the new DocumentRevision with a new revision ID
         try {
              updated = ds.database().update(retrieved);
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

     public  void deleteRevision(){
             // Delete

        // To delete a document, you need the current revision:
         try {
             ds.database().delete(updated);
         } catch (Exception e) {
             e.printStackTrace();
         }

     }



}
