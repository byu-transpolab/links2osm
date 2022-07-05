package org.byucivil.links2osm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.conveyal.osmlib.Node;
import com.conveyal.osmlib.OSM;
import com.conveyal.osmlib.Way;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Links2OsmConverter {

    private static final Logger logger = LoggerFactory.getLogger(Links2OsmConverter.class);

    HashMap<Integer, Node> nodeHashMap = new HashMap<>();
    HashMap<String, Way> wayHashMap = new HashMap<>();
    Counter nodesCounter = new Counter("Read node " );
    Counter linksCounter = new Counter("Read link ");
    OSM osm = null;

    String defaultType = null;

    public Links2OsmConverter(File nodesTable, File linksTable, File outFile) throws IOException {

        this.osm = new OSM(null);
        readNodes(nodesTable);
        readLinks(linksTable);

        osm.writeToFile(outFile.getPath());
    }


    public void setDefaultValues(){

        defaultType = "bike";
    }

    private void readNodes(File nodesTable) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(settings);
        parser.beginParsing(nodesTable);

        Record record;
        parser.getRecordMetadata();

        while ((record = parser.parseNextRecord()) != null) {
            long id = record.getInt("id");
            Double x = record.getDouble("x");
            Double y = record.getDouble("y");


            osm.nodes.put(id, new Node(y, x)); // needs to be in longitude, latitude. Gotten again!
            nodesCounter.incCounter();
        }
        logger.info("Read nodes: " + nodesCounter.getCounter());

    }

    private void readLinks(File linksFile) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(settings);
        parser.beginParsing(linksFile);


        Record record;
        parser.getRecordMetadata();

        while ((record = parser.parseNextRecord()) != null) {
            Way way = new Way();
            String id = record.getString("link_id");
            long start = record.getLong("a");
            long end = record.getLong("b");

            String lanes = "1";
            try{
               lanes = record.getString("lanes");
            } catch (Exception e){ }

            String capacity = "0";
            try{
                capacity = record.getString("capacity");
            } catch (Exception e) { }

            way.addTag("length", record.getString("length"));
            way.addTag("speed", record.getString("speed"));
            way.addTag("type", record.getString("type"));
            way.addTag("lanes", lanes);
            way.addTag("capacity", capacity);

            long[] nodes = new long[2];
            nodes[0] = start;
            nodes[1] = end;
            way.nodes = nodes;

            linksCounter.incCounter();
            osm.ways.put(linksCounter.getCounter(), way);
        }
        logger.info("Read links: " + linksCounter.getCounter());
    }


    public static void main(String[] args) throws Exception {
        File linksTable = new File(args[0]);
        File nodesTable = new File(args[1]);
        File outFile = new File(args[2]);

        logger.info("==== Link tables to OSM converter ====");
        logger.info("Links file: " + linksTable.toString());
        logger.info("Nodes file: " + nodesTable.toString());

        Links2OsmConverter converter = new Links2OsmConverter(
                nodesTable, linksTable, outFile);
    }

}
