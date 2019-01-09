import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

// New Imports
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.fs.Path;


import javax.crypto.KeyGenerator;
import java.io.*;
import java.util.*;

import static org.apache.hadoop.hbase.util.Writables.getBytes;


public class HBaseScrabble {
    private Configuration config;
    private HBaseAdmin hBaseAdmin;
    private static final String COMMA_DELIMITER = ",";
    private static final byte[] cfGame = Bytes.toBytes("game") ;
    private static final byte[] cfUser = Bytes.toBytes("user");
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

        HTableDescriptor table = new HTableDescriptor(TableName.valueOf(TABLE));
        HColumnDescriptor hcGame = new HColumnDescriptor(cfGame);
        HColumnDescriptor hcUser = new HColumnDescriptor(cfUser);

        hcGame.setMaxVersions(10);
        hcUser.setMaxVersions(10);
        //adding coloumn family to HTable
        table.addFamily(hcGame);
        table.addFamily(hcUser);
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
            //while (scanner.hasNext()){
            while (n<10000) {
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

                String n_aux = String.valueOf(n);

                Put p = new Put(Bytes.toBytes(tourneyid+n));
                p.add(cfGame, Bytes.toBytes("gameid"), Bytes.toBytes(gameid));
                p.add(cfGame, Bytes.toBytes("tourneyid"), Bytes.toBytes(tourneyid));
                p.add(cfGame, Bytes.toBytes("tie"), Bytes.toBytes(tie));
                p.add(cfUser, Bytes.toBytes("winnerid"), Bytes.toBytes(winnerid));
                p.add(cfUser, Bytes.toBytes("winnername"), Bytes.toBytes(winnername));
                p.add(cfUser, Bytes.toBytes("winnerscore"),Bytes.toBytes(winnerscore));
                p.add(cfUser, Bytes.toBytes("winneroldrating"), Bytes.toBytes(winneroldrating));
                p.add(cfUser, Bytes.toBytes("winnernewrating"), Bytes.toBytes(winnernewrating));
                p.add(cfUser, Bytes.toBytes("winnerpos"), Bytes.toBytes(winnerpos));
                p.add(cfUser, Bytes.toBytes("loserid"), Bytes.toBytes(loserid));
                p.add(cfUser, Bytes.toBytes("losername"), Bytes.toBytes(losername));
                p.add(cfUser, Bytes.toBytes("loserscore"), Bytes.toBytes(loserscore));
                p.add(cfUser, Bytes.toBytes("loseroldrating"), Bytes.toBytes(loseroldrating));
                p.add(cfUser, Bytes.toBytes("losernewrating"), Bytes.toBytes(losernewrating));
                p.add(cfUser, Bytes.toBytes("loserpos"), Bytes.toBytes(loserpos));
                p.add(cfGame, Bytes.toBytes("round"), Bytes.toBytes(round));
                p.add(cfGame, Bytes.toBytes("division"), Bytes.toBytes(division));
                p.add(cfGame, Bytes.toBytes("date"), Bytes.toBytes(date));
                p.add(cfGame, Bytes.toBytes("lexicon"), Bytes.toBytes(lexicon));

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
        Filter fTourneyid = new SingleColumnValueFilter(cfGame, Bytes.toBytes("tourneyid"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(tourneyid));
        Filter fWinnername = new SingleColumnValueFilter(cfUser, Bytes.toBytes("winnername"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(winnername));
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        filterList.addFilter(fTourneyid);
        filterList.addFilter(fWinnername);
        Scan scan = new Scan();
        scan.setFilter(filterList);
        ResultScanner rs = table.getScanner(scan);
        ArrayList<String> query1 = new ArrayList<>();
        for (Result r = rs.next(); r !=null; r = rs.next()) {

            byte[] value = r.getValue(cfUser, Bytes.toBytes("loserid"));
            String valueStr = new String(value);
            query1.add(valueStr);
            System.out.println(valueStr);
        }
        return query1;

    }

    public List<String> query2(String firsttourneyid, String lasttourneyid) throws IOException {
        //TO IMPLEMENT
        if (Integer.parseInt(firsttourneyid) > Integer.parseInt(lasttourneyid)) {
            String aux_1 = firsttourneyid;
            firsttourneyid = lasttourneyid;
            lasttourneyid = aux_1;
        }
        HConnection conn = HConnectionManager.createConnection(config);
        HTable table = new HTable(TableName.valueOf("ScrabbleGames"), conn);

        byte[] first_key = (firsttourneyid).getBytes();

        //byte[] last_key = (lasttourneyid+"z").getBytes();
        byte[] last_key = (lasttourneyid+".").getBytes();

        Scan scan = new Scan(first_key,last_key);
        ResultScanner scanner = table.getScanner(scan);
        ArrayList<String> ids = new ArrayList<>();
        Set<String> query2_aux = new HashSet<>();
        Set<String> set = new HashSet<>();
        int n = 0;
        for (Result r = scanner.next(); r !=null; r = scanner.next()) {
            byte[] winner_aux = r.getValue(cfUser, Bytes.toBytes("winnerid"));
            String winnerId = new String(winner_aux);
            byte[] loser_aux = r.getValue(cfUser, Bytes.toBytes("loserid"));
            String loserId = new String(loser_aux);
            byte[] tourneyid_aux = r.getValue(cfGame, Bytes.toBytes("tourneyid"));
            String tourneyid = new String(tourneyid_aux);

            if (Integer.parseInt(firsttourneyid)<=Integer.parseInt(tourneyid) && (Integer.parseInt(lasttourneyid)>=Integer.parseInt(tourneyid))) {
                ids.add(winnerId);
                ids.add(loserId);
                n++;
                //System.out.println("####### --> "+n);
            }

        }

        for (String i: ids) {
            if(set.add(i) == false) {
                query2_aux.add(i);
            }
        }

        ArrayList<String> query2 = new ArrayList<>(query2_aux);
        return query2;

    }

    public List<String> query3(String tourneyid) throws IOException {
        //TO IMPLEMENT
        HConnection conn = HConnectionManager.createConnection(config);
        HTable table = new HTable(TableName.valueOf("ScrabbleGames"), conn);
        Filter fTourneyid = new SingleColumnValueFilter(cfGame, Bytes.toBytes("tourneyid"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(tourneyid));
        //Filter fTie = new SingleColumnValueFilter(Bytes.toBytes("game"), Bytes.toBytes("winnername"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(true));
        //FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        //filterList.addFilter(fTourneyid);
        //filterList.addFilter(fTie);
        Scan scan = new Scan();
        scan.setFilter(fTourneyid);
        ResultScanner rs = table.getScanner(scan);
        ArrayList<String> query3 = new ArrayList<>();
        for (Result r = rs.next(); r !=null; r = rs.next()) {
            byte[] gameid_aux = r.getValue(cfGame, Bytes.toBytes("gameid"));
            byte[] tie_aux = r.getValue(cfGame, Bytes.toBytes("tie"));
            String gameid = new String(gameid_aux);
            Boolean tie = Bytes.toBoolean(tie_aux);

            if(tie) {
                query3.add(gameid);
            }
        }
        return query3;
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
