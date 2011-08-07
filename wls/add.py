#!/usr/bin/python
# usage:             1        2         3        
# create_node.py adminURL machineHost machineIP
import sys, socket



def getHostIp():
   return (socket.gethostname(), socket.gethostbyname(socket.gethostname()) )
   

def createServer(argv):

   adminURL=argv[1]
   machineHost=argv[2]
   machineIP=argv[3]
   managedServerName='ms_' + machineHost

   connect(url=adminURL) # storeUserConfig() miatt

   edit()
   startEdit()

   cd('/')
   cmo.createUnixMachine(machineHost)

   cd('/Machines/'+machineHost +'/NodeManager/'+machineHost)
   cmo.setNMType('SSL')
   cmo.setListenAddress(machineIP)
   cmo.setListenPort(5556)
   cmo.setDebugEnabled(false)

   cd('/')
   cmo.createServer(managedServerName)
   cd('/Servers/' + managedServerName)
   cmo.setListenAddress(machineIP)
   cmo.setListenPort(7101)
   cmo.setCluster(getMBean('/Clusters/c1'))
   cmo.setMachine(getMBean('/Machines/'+machineHost))

   save()
   activate()

   start(managedServerName,"Server",block='true')


#todo
def deleteServer(argv):

   adminURL=argv[1]
   machineHost=argv[2]
   machineIP=argv[3]
   managedServerName='ms_' + machineHost

   connect(url=adminURL) # storeUserConfig() miatt


   #shutdown(managedServerName,'Server','true',5000,block='true')
   edit()
   startEdit()

   cd('/')
   
   cd('/Servers/'+managedServerName)
   cmo.setCluster(None)
   cmo.setMachine(None)
   
   editService.getConfigurationManager().removeReferencesToBean(getMBean('/Servers/'+managedServerName))
   
   cd('/')
   cmo.destroyServer(getMBean('/Servers/'+managedServerName))
   
   editService.getConfigurationManager().removeReferencesToBean(getMBean('/Machines/'+machineHost))
   cmo.destroyMachine(getMBean('/Machines/'+machineHost))
   
   
   save()
   activate()




arg = sys.argv

if len(arg)<2:
   arg.append('start')
   
if len(arg)<3:
   arg.append("admin:7001")

if len(arg)<5:
   (host,ip) = getHostIp()
   arg.append(host)
   arg.append(ip)

if arg[1]=='start':
   arg.pop(0)
   createServer(arg)
elif arg[1]=='stop':
   arg.pop(0)
   deleteServer(arg)
else:
   print "Usage: add.py {start|stop} [adminUrl] [host] [ip]"

