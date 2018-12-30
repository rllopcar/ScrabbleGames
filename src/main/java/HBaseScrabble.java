import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

// New Imports
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.mapreduce.;


import javax.crypto.KeyGenerator;
import java.io.*;
//import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class HBaseScrabble {
    private Configuration config;
    private HBaseAdmin hBaseAdmin;
    private static final String COMMA_DELIMITER = ",";
    /**
     * The Constructor. Establishes the connection with HBase.
     * @param zkHost
     * @throws IOException
     */
    public HBaseScrabble(String zkHost) throws IOException {
        // Instances the configuration file, hbase-site.xml
        config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", zkHost.split(":")[0]);
        config.set("hbase.zookeeper.property.clientPort", zkHost.split(":")[1]);
        HBaseConfiguration.addHbaseResources(config);
        //Management API: create table, drop table, disable table
        this.hBaseAdmin = new HBaseAdmin(config);
    }

    public void createTable() throws IOException {
        //Instantianting table descriptor class
        byte[] TABLE = Bytes.toBytes("ScrabbleGames");
        byte[] CF = Bytes.toBytes("game");
        HTableDescriptor table = new HTableDescriptor(TableName.valueOf(TABLE));
        HColumnDescriptor family = new HColumnDescriptor(CF);
        family.setMaxVersions(10);
        //adding coloumn family to HTable
        table.addFamily(family);
        hBaseAdmin.createTable(table);

        System.exit(-1);
    }

    public void loadTable(String folder)throws IOException{
        //TO IMPLEMENT
        Scanner scanner = null;
        int n = 0;
        Object[] arr = new Object[19];
        try {
            HConnection conn = HConnectionManager.createConnection(config);
            scanner = new Scanner((new File(folder+"/scrabble_games.csv")));
            scanner.useDelimiter(COMMA_DELIMITER);
            scanner.nextLine();
            while (scanner.hasNext()){
                String nextLine = scanner.nextLine();
                String[] game = nextLine.split(COMMA_DELIMITER);
                String gameid = game[0];
                String tourneyid = game[1];
                Boolean tie = Boolean.parseBoolean(game[2]);
                String winnerid = game[3];
                String winnername = game[4];
                String winnerscore = game[5];
                String winneroldrating = game[6];
                String winnernewrating = game[7];
                String winnerpos = game[8];
                String loserid = game[9];
                String losername = game[10];
                String loserscore = game[11];
                String loseroldrating = game[12];
                String losernewrating = game[13];
                String loserpos = game[14];
                String round = game[15];
                String division = game[16];
                String date = game[17];
                Boolean lexicon = Boolean.parseBoolean(game[18]);

                Put p = new Put(Bytes.toBytes(n));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("gameid"),Bytes.toBytes(gameid));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("tourneyid"),Bytes.toBytes(tourneyid));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("tie"),Bytes.toBytes(tie));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("winnerid"),Bytes.toBytes(winnerid));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("winnername"),Bytes.toBytes(winnername));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("winnerscore"),Bytes.toBytes(winnerscore));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("winneroldrating"),Bytes.toBytes(winneroldrating));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("winnernewrating"),Bytes.toBytes(winnernewrating));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("winnerpos"),Bytes.toBytes(winnerpos));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("loserid"),Bytes.toBytes(loserid));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("losername"),Bytes.toBytes(losername));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("loserscore"),Bytes.toBytes(loserscore));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("loseroldrating"),Bytes.toBytes(loseroldrating));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("losernewrating"),Bytes.toBytes(losernewrating));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("loserpos"),Bytes.toBytes(loserpos));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("round"),Bytes.toBytes(round));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("division"),Bytes.toBytes(division));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("date"),Bytes.toBytes(date));
                p.add(Bytes.toBytes("game"),Bytes.toBytes("lexicon"),Bytes.toBytes(lexicon));

                HTable table = new HTable(TableName.valueOf("ScrabbleGames"), conn);
                table.put(p);
                System.out.println("#############"+n);
                table.close();
                n++;
            }

        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        }
        System.exit(-1);
    }

    /**
     * This method generates the key
     * @param values The value of each column
     * @param keyTable The position of each value that is required to create the key in the array of values.
     * @return The encoded key to be inserted in HBase
     */
    private byte[] getKey(String[] values, int[] keyTable) {
        String keyString = "";
        for (int keyId : keyTable){
            keyString += values[keyId];
        }
        byte[] key = Bytes.toBytes(keyString);

        return key;
    }



    public List<String> query1(String tourneyid, String winnername) throws IOException {
        //TO IMPLEMENT
        HConnection conn = HConnectionManager.createConnection(config);
        HTable table = new HTable(TableName.valueOf("ScrabbleGames"), conn);
        Filter fTourneyid = new SingleColumnValueFilter(Bytes.toBytes("game"), Bytes.toBytes("tourneyid"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(tourneyid));
        Filter fWinnername = new SingleColumnValueFilter(Bytes.toBytes("game"), Bytes.toBytes("winnername"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(winnername));
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        filterList.addFilter(fTourneyid);
        filterList.addFilter(fWinnername);
        Scan scan = new Scan();
        scan.setFilter(filterList);
        ResultScanner rs = table.getScanner(scan);
        ArrayList<String> query1 = new ArrayList<>();
        for (Result r = rs.next(); r !=null; r = rs.next()) {
            byte[] value = r.getValue(Bytes.toBytes("game"), Bytes.toBytes("loserid"));
            String valueStr = new String(value);
            query1.add(valueStr);
        }
        return query1;

    }

    public List<String> query2(String firsttourneyid, String lasttourneyid) throws IOException {
        //TO IMPLEMENT
        System.exit(-1);
        return null;
    }

    public List<String> query3(String tourneyid) throws IOException {
        //TO IMPLEMENT
        System.exit(-1);
        return null;
    }


    public static void main(String[] args) throws IOException {
        if(args.length<2){
            System.out.println("Error: \n1)ZK_HOST:ZK_PORT, \n2)action [createTable, loadTable, query1, query2, query3], \n3)Extra parameters for loadTables and queries:\n" +
                    "\ta) If loadTable: csvsFolder.\n " +
                    "\tb) If query1: tourneyid winnername.\n  " +
                    "\tc) If query2: firsttourneyid lasttourneyid.\n  " +
                    "\td) If query3: tourneyid.\n  ");
            System.exit(-1);
        }
        HBaseScrabble hBaseScrabble = new HBaseScrabble(args[0]);
        if(args[1].toUpperCase().equals("CREATETABLE")){
            hBaseScrabble.createTable();
        }
        else if(args[1].toUpperCase().equals("LOADTABLE")){
            if(args.length!=3){
                System.out.println("Error: 1) ZK_HOST:ZK_PORT, 2)action [createTables, loadTables], 3)csvsFolder");
                System.exit(-1);
            }
            else if(!(new File(args[2])).isDirectory()){
                System.out.println("Error: Folder "+args[2]+" does not exist.");
                System.exit(-2);
            }
            hBaseScrabble.loadTable(args[2]);
        }
        else if(args[1].toUpperCase().equals("QUERY1")){
            if(args.length!=4){
                System.out.println("Error: 1) ZK_HOST:ZK_PORT, 2)query1, " +
                        "3) tourneyid 4) winnername");
                System.exit(-1);
            }

            List<String> opponentsName = hBaseScrabble.query1(args[2], args[3]);
            System.out.println("There are "+opponentsName.size()+" opponents of winner "+args[3]+" that play in tourney "+args[2]+".");
            System.out.println("The list of opponents is: "+Arrays.toString(opponentsName.toArray(new String[opponentsName.size()])));
        }
        else if(args[1].toUpperCase().equals("QUERY2")){
            if(args.length!=4){
                System.out.println("Error: 1) ZK_HOST:ZK_PORT, 2)query2, " +
                        "3) firsttourneyid 4) lasttourneyid");
                System.exit(-1);
            }
            List<String> playerNames =hBaseScrabble.query2(args[2], args[3]);
            System.out.println("There are "+playerNames.size()+" players that participates in more than one tourney between tourneyid "+args[2]+" and tourneyid "+args[3]+" .");
            System.out.println("The list of players is: "+Arrays.toString(playerNames.toArray(new String[playerNames.size()])));
        }
        else if(args[1].toUpperCase().equals("QUERY3")){
            if(args.length!=3){
                System.out.println("Error: 1) ZK_HOST:ZK_PORT, 2) query3, " +
                        "3) tourneyid");
                System.exit(-1);
            }
            List<String> games = hBaseScrabble.query3(args[2]);
            System.out.println("There are "+games.size()+" that ends in tie in tourneyid "+args[2]+" .");
            System.out.println("The list of games is: "+Arrays.toString(games.toArray(new String[games.size()])));
        }
        else{
            System.out.println("Error: \n1)ZK_HOST:ZK_PORT, \n2)action [createTable, loadTable, query1, query2, query3], \n3)Extra parameters for loadTables and queries:\n" +
                    "\ta) If loadTable: csvsFolder.\n " +
                    "\tb) If query1: tourneyid winnername.\n  " +
                    "\tc) If query2: firsttourneyid lasttourneyid.\n  " +
                    "\td) If query3: tourneyid.\n  ");
            System.exit(-1);
        }

    }



}
