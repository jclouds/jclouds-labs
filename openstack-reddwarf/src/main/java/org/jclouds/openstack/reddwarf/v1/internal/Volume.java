package org.jclouds.openstack.reddwarf.v1.internal;

public class Volume{
   private final int size;
   
   public Volume(int size){
      this.size = size;
   }

   /**
    * @return the size
    */
   public int getSize() {
      return size;
   }
}
