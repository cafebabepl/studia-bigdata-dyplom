gawk -f convert-nodes-to-gdf.awk ../../../data/out/nodes/part-00000 >  ../../../data/out/graph.gdf
gawk -f convert-edges-to-gdf.awk ../../../data/out/edges/part-00000 >> ../../../data/out/graph.gdf
pause