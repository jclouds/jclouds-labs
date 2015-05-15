jclouds Labs OpenStack
======================

Repository for developing OpenStack providers and APIs

Although this is a "labs" repository, some of the providers and APIs here are production ready. The reason they have not been merged into into jclouds repository is because there isn't a suitable abstraction layer for them yet. APIs that we expect to change will be marked @Beta.

### Summary

| Name | Type |
|------|------|
| openstack-glance | API |
| openstack-marconi | API |
| openstack-neutron | API |
| openstack-poppy | API |
| openstack-heat | API |
| rackspace-autoscale | API |
| rackspace-autoscale-us | Provider |
| rackspace-cloudqueues-us | Provider |
| rackspace-cloudbigdata-us | Provider |
| rackspace-cloudnetworks-us | Provider |
| rackspace-cdn-us | Provider |

This is how providers map to the respective APIs they use:

| Provider | Api |
|----------|-----|
| rackspace-cloudnetworks-us | openstack-neutron |
| rackspace-cnd-us | openstack-poppy |
| rackspace-cloudqueues-us | openstack-marconi |
| rackspace-autoscale-us | rackspace-autoscale |
| rackspace-cloudbigdata-us | rackspace-cloudbigdata |

APIs new to jclouds are marked as Beta. That means we need people to use it and give us feedback. Based on that feedback, minor changes to the interfaces may happen. It is recommended you adopt this code sooner than later.

How long APIs are in Beta is variable but it will be at minimum one release.

License
-------
Copyright (C) 2009-2014 The Apache Software Foundation

Licensed under the Apache License, Version 2.0
