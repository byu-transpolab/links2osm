# links2osm
A basic java library to construct osm files from link and node tables

Lots of open-source tools have been made that rely on data stored in OpenStreetMap. 
Unfortunately, OpenStreetMap at its core is a *map*, and not a *network*, though people
often confuse the two or pretend that a map is sufficient.
At the same time, MPO's, DOT's, and many other entitities have invested lots of time
generating network information that cannot be easily transferred onto an OpenStreetMap-derived
network.

This is a small library that contains Java code to take an existing travel model 
network (in the form of link and node `csv` files)
and convert it into an OpenStreetMap `osm.pbf` file.

## Use

As of now, the program is quite rigid and inflexible. The program is called as an executable jar,

```sh
java -jar links2osm-1.0-SNAPSHOT.jar links.csv nodes.csv net.osm.pbf
```

### Links file
`links.csv` is a table of the links in the network. 

### Nodes file
`links.csv` is a table of the links in the network. The table must have a header row with 
the following names: 

  - `id` Node ID value, corresponding to the values in `a` and `b` in the links table.
  - `x`: Longitude of the node
  - `y`: Latitutde of the node
