alibaba Elastic Compute Service Provider
==========================

# How to use it

alibaba ECS provider works exactly as any other jclouds provider.
Notice that as alibaba supports dozens of locations and to limit the scope of some operations, one may want to use:

and
```bash
jclouds.regions
```
which is by default `null`. If you want to target only the `north europe` region, you can use

```bash
jclouds.regions="eu-central-1"
```

# Setting Up Test Environment

Get or create the `User Access Key` and `Access Key Secret` for your account at `https://usercenter.console.alibaba.com/#/manage/ak`

# Run Live Tests

Use the following to run one live test:

```bash
mvn -Dtest=<name of the live test> \
    -Dtest.alibaba-ecs.identity="<AccessKey ID>" \
    -Dtest.alibaba-ecs.credential="<Access Key Secret>" 
    integration-test -Plive
```

Use the following to run all the live tests:

```bash

mvn clean verify -Plive \
    -Dtest.alibaba-ecs.identity="<AccessKey ID>" \
    -Dtest.alibaba-ecs.credential="<Access Key Secret>" 
```


