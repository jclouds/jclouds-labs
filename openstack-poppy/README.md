#OpenStack Poppy
================

OpenStack Poppy is a modular, vendor-neutral API, that wraps provisioning instructions for all CDN vendors that support it.

- REST API for CDN service provisioning
- Multi-tenant
- Integrated with Keystone for Authentication
- Integrated with Designate for dynamic CNAMEing
- Support for CDN providers (Fastly, MaxCDN, CloudFront, Akamai, Edgecast)

##Running Live Tests
===
To run the Poppy live tests, execute the following command with your credentials and authentication endpoint:

    $ mvn clean install -Plive -Dtest.openstack-poppy.identity=<username> -Dtest.openstack-poppy.credential=<password> -Dtest.openstack-poppy.endpoint=<keystone-auth-url>

##Production ready?
===
No. The OpenStack Poppy API is a beta API and is subject to change during it's development. APIs have `@Beta` annotations where applicable.

##Project Links
===
[Wiki](https://wiki.openstack.org/wiki/Poppy)

[PoppyCDN](http://www.poppycdn.org/)

[Sources](https://github.com/stackforge/poppy)
