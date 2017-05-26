BEGIN {
	FS = ";"
	OFS = ","
	print "nodedef>name VARCHAR,weight DOUBLE"
}

{
	gsub(/,/, "", $1)
	print $1,$2
}