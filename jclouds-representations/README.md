jclouds representations
=======================

This project provides representations for the jclouds domain objects.
The representation object have no dependency in the acutal jclouds modules and can be used as a "thin client" from
other software that wants to integrate with jclouds.

The original intention was to be used for JMX integration but can also be used for REST etc.

The project contains two submodules:

* representations-core
* represtations-codec

representations-core
--------------------

Contains representation objects for core, compute & blobstore modules of jclouds.


representations-codec
---------------------

Contains convertion functions for the actual jclouds domain objects to their representations.
It also includes a ComputeService and BlobStore interface which use the representation instead of the domain objects.

**Note:** This module does depend from jclouds.