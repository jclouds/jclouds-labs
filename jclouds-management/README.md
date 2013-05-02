jclouds management
=======================

This project provides integration of jclouds with JMX.

Jclouds Managed Beans
---------------------

The project provides 3 kind of MBeans:

* **JcloudsManagementMBean** (single) Exposes Apis, Providers & Contexts. Additionally provides methods for creating Contexts.
* **ComputeServiceManagementMBean** (per context) Exposes all ComputeService operations via JMX.
* **BlobstoreManagementMBean** (per context) Exposes all Blobstore operations via JMX.


The ManagementContext
---------------------

The ManagementContext is resoposible for keeping track of the MBeanServer, MBean and Contexts LifeCycle.
The default implementation is the BaseManagementContext and it provides method for binding and unbinding an MBeanServer and also jclouds ManagedBean.

Below is an example that create the ManagementContext and registers the core jclouds management bean.

    ManagementContext managementContext = new BaseManagementContext();
    JcloudsManagementMBean jcloudsManagement = new JcloudsManagement();
    managementContext.manage(jcloudsManagement);

This will expose via JMX the available Apis, Providers and Contexts. Also it will provide managed methods for creating a Context.
When used inside OSGi the BaseManagementContext is registered as a service, JcloudsManagementMBean will be autoregistered and it will transparently track MBeanServer changes.

The ManagementLifeCycle module
-------------------------------

The jclouds management project provides a guice module called ManagementLifecycle. This module can be passed to the ContextBuilder in order to expose mbeans for the created contexts.

    ManagmenetContext managementContext = new BaseManagementContext();
    ContextBuilder.newBuilder(providerOrApi).modules(ImmutableSet.of(new ManagementLifecycle(managementContext)).build();


