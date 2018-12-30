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

import static org.apache.hadoop.hbase.util.Bytes.toBytes;


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
        byte[] TABLE = toBytes("ScrabbleGames");
        byte[] CF = toBytes("game");
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

                Put p = new Put(toBytes(n));
                p.add(toBytes("game"), toBytes("gameid"), toBytes(gameid));
                p.add(toBytes("game"), toBytes("tourneyid"), toBytes(tourneyid));
                p.add(toBytes("game"), toBytes("tie"), toBytes(tie));
                p.add(toBytes("game"), toBytes("winnerid"), toBytes(winnerid));
                p.add(toBytes("game"), toBytes("winnername"), toBytes(winnername));
                p.add(toBytes("game"), toBytes("winnerscore"), toBytes(winnerscore));
                p.add(toBytes("game"), toBytes("winneroldrating"), toBytes(winneroldrating));
                p.add(toBytes("game"), toBytes("winnernewrating"), toBytes(winnernewrating));
                p.add(toBytes("game"), toBytes("winnerpos"), toBytes(winnerpos));
                p.add(toBytes("game"), toBytes("loserid"), toBytes(loserid));
                p.add(toBytes("game"), toBytes("losername"), toBytes(losername));
                p.add(toBytes("game"), toBytes("loserscore"), toBytes(loserscore));
                p.add(toBytes("game"), toBytes("loseroldrating"), toBytes(loseroldrating));
                p.add(toBytes("game"), toBytes("losernewrating"), toBytes(losernewrating));
                p.add(toBytes("game"), toBytes("loserpos"), toBytes(loserpos));
                p.add(toBytes("game"), toBytes("round"), toBytes(round));
                p.add(toBytes("game"), toBytes("division"), toBytes(division));
                p.add(toBytes("game"), toBytes("date"), toBytes(date));
                p.add(toBytes("game"), toBytes("lexicon"), toBytes(lexicon));

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
        byte[] key = toBytes(keyString);

        return key;
    }



    public List<String> query1(String tourneyid, String winnername) throws IOException {
        //TO IMPLEMENT
        HConnection conn = HConnectionManager.createConnection(config);
        HTable table = new HTable(TableName.valueOf("ScrabbleGames"), conn);
        Filter fTourneyid = new SingleColumnValueFilter(toBytes("game"), toBytes("tourneyid"), CompareFilter.CompareOp.EQUAL, toBytes(tourneyid));
        Filter fWinnername = new SingleColumnValueFilter(toBytes("game"), toBytes("winnername"), CompareFilter.CompareOp.EQUAL, toBytes(winnername));
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        filterList.addFilter(fTourneyid);
        filterList.addFilter(fWinnername);
        Scan scan = new Scan();
        scan.setFilter(filterList);
        ResultScanner rs = table.getScanner(scan);
        ArrayList<String> query1 = new ArrayList<>();
        for (Result r = rs.next(); r !=null; r = rs.next()) {
            byte[] value = r.getValue(toBytes("game"), toBytes("loserid"));
            String valueStr = new String(value);
            query1.add(valueStr);
        }
        return query1;

    }

    public List<String> query2(String firsttourneyid, String lasttourneyid) throws IOException {
        //TO IMPLEMENT
        HConnection conn = HConnectionManager.createConnection(config);
        HTable table = new HTable(TableName.valueOf("ScrabbleGames"), conn);
        Filter first = new SingleColumnValueFilter(toBytes("game"), toBytes("tourneyid"), CompareFilter.CompareOp.EQUAL, toBytes(firsttourneyid));
        Filter last = new SingleColumnValueFilter(toBytes("game"), toBytes("tourneyid"), CompareFilter.CompareOp.EQUAL, toBytes(lasttourneyid));

        Scan scan_1 = new Scan();
        Scan scan_2 = new Scan();

        scan_1.setFilter(first);
        scan_2.setFilter(last);

        ResultScanner rs_1 = table.getScanner(scan_1);
        ResultScanner rs_2 = table.getScanner(scan_2);

        ArrayList<String> rk_1 = new ArrayList<>();
        ArrayList<String> rk_2 = new ArrayList<>();

        for (Result r = rs_1.next(); r !=null; r = rs_1.next()) {
            byte[] aux_key = r.getRow();
            String aux_key_2 = new String(aux_key);
            rk_1.add(aux_key_2);
        }

        for (Result r = rs_2.next(); r !=null; r = rs_2.next()) {
            byte[] aux_key = r.getRow();
            String aux_key_2 = new String(aux_key);
            rk_2.add(aux_key_2);
        }

        String first_key = rk_1.get(0);
        String last_key = rk_2.get(rk_2.size()-1);
        Get g = new Get(toBytes(first_key));
        Result result = table.get(g);
        byte[] value = result.getValue(Bytes.toBytes("game"), Bytes.toBytes("winnername"));
        String name = Bytes.toString(value);
        System.out.println("EL nombre es --> "+name);


        Get gg = new Get(toBytes(last_key));
        Result resultt = table.get(gg);
        byte[] valuee = resultt.getValue(Bytes.toBytes("game"), Bytes.toBytes("winnername"));
        String namee = Bytes.toString(valuee);
        System.out.println("EL nombre es --> "+namee);

        Scan scan = new Scan(Bytes.toBytes(first_key),Bytes.toBytes(last_key));
        ResultScanner scanner = table.getScanner(scan);

        for (Result r = scanner.next(); r !=null; r = scanner.next()) {

            // byte[] value_1 = r.getValue(Bytes.toBytes("game"), Bytes.toBytes("winnerid"));
            //byte[] value_2 = r.getValue(Bytes.toBytes("game"), Bytes.toBytes("loserid"));

            //String winnerId = new String(value_1);
            //String loserId = new String(value_2);

            //aux_1.add(winnerId);
            //aux_1.add(loserId);
            //System.out.println("############");
            //System.out.println("WINNER ID IS --> "+winnerId);
            //System.out.println("LOSER ID IS ---> "+loserId);
        }




        //for(int i=0; i<aux_1.size(); i++ ) {
        //    System.out.println("########"+aux_1.get(i));
        //}

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
