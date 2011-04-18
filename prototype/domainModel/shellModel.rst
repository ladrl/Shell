===========
Shell Model
===========

Shell should be based on a set of models to perform its tasks. Each model will interact with others across the domains.

Shell, in its essence, must allow a user to interact with an arbitrary set of resources in an efficent way.

Domains
*******

The domains which should be modelled:

 - Resource
   Model of whatever the user should be able to modify in the sense of getting some work done
   
 - Event/Signal
   Model of how the intentions and whishes get from the user to the resource model and how the results and consequences get back to the user
   
 - User
   Model of the user itself, his identity, normal behavior, presets and so on
   
.. Graphviz::
 digraph {
  node [shape=box];
  user[label="User"];
  resource[label="Resource"];
  user -> resource[label="Event"];
  resource -> user[label="Signal"];
 }

Domains in detail
*****************

User
----

The general idea is for the User Model to act as the proxy of the "real" user sitting in front of the computer. It carries all user specific data which includes:

  - Configurations
   
   - What to show
   - Where to search
   - Which shortcut means what
   - ...
   
  - Layout & visuals
   
   - How to display status
   - How to inform the user
   - 
   
  - Habits
   
