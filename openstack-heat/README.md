#OpenStack Heat
================

OpenStack Heat is a template-driven engine that allows application developers to describe and automate the deployment of infrastructure. The flexible template language can specify compute, storage and networking configurations as well as detailed post-deployment activity to automate the full provisioning of infrastructure as well as services and applications.

##Running Live Tests
===
To run the Heat live tests, execute the following command with your credentials and authentication endpoint:

    $ mvn clean install -Plive -Dtest.openstack-heat.identity=<username> -Dtest.openstack-heat.credential=<password> -Dtest.openstack-heat.endpoint=<keystone-auth-url>

##Production ready?
===
No. The OpenStack Heat API is a beta API and is subject to change during it's development. APIs have `@Beta` annotations where applicable.
