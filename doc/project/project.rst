=============
Project Shell
=============

Motivation
**********

 - Power user tool
 - Edit & control in one place
 - Have a tool with the power and the reativity of a commandline, but with the richness of a full application

Mottos
******


Nicht immer schnell sondern nie langsam.

Der User hat das kommando: reaktion sofort, auch wenn nicht alles geladen.

Visuelle indikation von Tastaturkommands

Plugins müssen deklarieren, welche intents sie akzeptieren und welche sie generieren.

Feature proposal
****************

 - Customizable --> Profile (perhaps activable by rules based on resources)

  * looks
  * keys
  * whatnot...

 - Extendable

  * OSGi based
  * service resolution based on Intents
  * Grouping mechanism for functionality --> Register, Scope

 - GUI with the premisse: as simple as possible, as complicated as necessary

  * Grid-like paradigm: Based on tiles (like in tileing), the user can choose the arragement

 - Workflow-oriented

  * Any messagebox must have default config

 - Resource-oriented
   Resources could be:

    * Files, Directories
    * Applications
    * Configurations
    * Connections
    * Databases

 - Scope & bind as concepts

   * Scope defines the accessible resources
     --> Requires some means of structuring --> i.e. Topology metaphor
   * Bind allows renaming of resources

 - Metaphors

  * Resource (Everything one works with)
  * Topology (How to find things)
  * Intent (What do you want to do?) --> Android

   * General intent --> Fulfillable by any plugin
   * Specific Intcnt --> Fulfillabe by a specific plugin only

  * Workflow

   * Compose intents to a workflow



