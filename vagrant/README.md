Vagrant provider for jclouds
============================

Building
--------

  * `git clone https://github.com/jclouds/jclouds-labs`
  * `cd jclouds-labs/vagrant`
  * `mvn clean install`
  * Copy `target/vagrant-2.0.0-SNAPSHOT.jar` to your classpath

Local caching proxy
-------------------

### Polipo

Use `polipo` for caching proxy. On OS X install with

```
brew install polipo
```

From [SO](http://superuser.com/questions/192696/how-can-i-make-tor-and-polipo-run-and-automatically-restart-using-launchd-on-m):

* Create a config file at ~/.polipo/config

```
# logLevel = 0xFF
dnsNameServer=8.8.8.8
diskCacheRoot = "~/.polipo/cache/"

```

* As root create the file `/Library/LaunchDaemons/fr.jussieu.pps.polipo.plist`, replace $USER with your username:
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Disabled</key>
    <false/>
        <key>Label</key>
        <string>fr.jussieu.pps.polipo</string>
        <key>ProgramArguments</key>
        <array>
                <string>/usr/local/bin/polipo</string>
                <string>-c</string>
                <string>/Users/$USER/.polipo/config</string>
        </array>
        <key>RunAtLoad</key>
        <true/>
    <key>OnDemand</key>
    <false/>
    <key>UserName</key>
    <string>$USER</string>
    <key>GroupName</key>
    <string>daemon</string>
    <key>StandardOutPath</key>
    <string>/Users/$USER/.polipo/polipo.log</string>
    <key>StandardErrorPath</key>
    <string>/Users/$USER/.polipo/polipo.log</string>
</dict>
</plist>
```

* `sudo chown root:wheel /Library/LaunchDaemons/fr.jussieu.pps.polipo.plist`
* `sudo chmod 755 /Library/LaunchDaemons/fr.jussieu.pps.polipo.plist`
* `sudo launchctl load -w /Library/LaunchDaemons/fr.jussieu.pps.polipo.plist`

### Vagrant

* `vagrant plugin install vagrant-proxyconf`
* add to `~/.vagrant.d/Vagrantfile`:

```
Vagrant.configure("2") do |config|
  if Vagrant.has_plugin?("vagrant-proxyconf")
    config.proxy.http     = "http://10.0.2.2:8123/"
    config.proxy.https    = "http://10.0.2.2:8123/"
    config.proxy.no_proxy = "localhost,127.0.0.1"
  end
end
```

Where `10.0.2.2` is the IP of your host as seen from the vagrant machines (in this case the NAT interface).
Optionally could add all your private network IPs from your Vagrant subnet to `no_proxy` to skip the proxy for inter-VM communications.

Testing
-----------

```
mvn clean install -Plive
```

Cleaning up
-----------

Sometimes users (or tests) do not stop correctly the machines so they need to be destroyed manually periodically.
All machines live in `~/.jclouds/vagrant`. Create `cleanup.sh` in the folder and execute it to destroy machines created by the provider:

```
for node in `find ~/.jclouds/vagrant -name Vagrantfile | xargs -n1 dirname`; do
  pushd $node > /dev/null
  echo Destroying $node
  vagrant destroy --force
  popd> /dev/null
  rm -rf $machine
done
```

Same as a one-liner

```
for f in `find ~/.jclouds/vagrant/tests -name Vagrantfile | xargs -n1 dirname`; do pushd $f; vagrant destroy --force; popd; rm -rf $f; done
```


Limitations
-----------

* Machines are created sequentially, no support for parallel execution from virtualbox provider
* Something prevents using vagrant at the same time with other jclouds providers - they try to login with vagrant user.
