BEGIN {
	FS = ";"
	OFS = ""
	print "["
}

{
	gsub(/'/, "", $1)
	print "['", $1, "',", $2, "],"
}

END {
	print "]"
}