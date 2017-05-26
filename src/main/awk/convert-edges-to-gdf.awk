BEGIN {
	FS = ";"
	OFS = ","
	print "edgedef>node1 VARCHAR,node2 VARCHAR,weight DOUBLE"
}

{
	gsub(/,/, "", $1)
	gsub(/,/, "", $2)
	print $1,$2,$3
}