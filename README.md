jclouds Labs OpenStack
======================

Repository for developing OpenStack providers and APIs

Although this is a "labs" repository, some of the providers and APIs here are production ready. The reason they have not been merged into into jclouds repository is because there isn't a suitable abstraction layer for them yet. Please review the README in each sub-directory to determine if the provider or API is production ready.

### Summary

| Name | Type | Production Ready |
|------|------|------------------|
| openstack-glance | API | No |
| openstack-marconi | API | No |
| openstack-neutron | API | Yes |
| rackspace-autoscale | API | No |
| rackspace-cloudfiles | API | No |
| rackspace-autoscale-us | Provider | No |
| rackspace-cloudfiles-uk | Provider | Beta |
| rackspace-cloudfiles-us | Provider | Beta |
| rackspace-cloudqueues-us | Provider | No |

APIs new to jclouds are marked as Beta. That means we need people to use it and give us feedback. Based on that feedback, minor changes to the interfaces may happen. It is recommended you adopt this code sooner than later.

How long APIs are in Beta is variable but it will be at minimum one release.

License
-------
Copyright (C) 2009-2014 The Apache Software Foundation

Licensed under the Apache License, Version 2.0
