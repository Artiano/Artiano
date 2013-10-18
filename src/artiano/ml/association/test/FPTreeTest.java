package artiano.ml.association.test;

import java.util.*;

import artiano.ml.association.FPTree;
import artiano.ml.association.FPTreeNode;

public class FPTreeTest {

	
	public void testReadTransRecord() {
		FPTree tree = new FPTree();
		List<List<String>> transactions =
			tree.readTransactionRecord("src\\artiano\\ml\\association\\test\\data2.txt");
		for(List<String> transaction: transactions) {
			for(String item : transaction) {
				System.out.print(item + "\t");
			}
			System.out.println();
		}
		
	}

	
	public void testFindFrequentOneItemset() {
		FPTree tree = new FPTree();
		int minSupport = 3;
		tree.setMinSupport(minSupport);
		List<List<String>> transactions =
			tree.readTransactionRecord("src\\artiano\\ml\\association\\test\\data2.txt");
		List<FPTreeNode> frequent1Itemset =
			tree.buildHeaderTable(transactions);
		for(FPTreeNode node : frequent1Itemset) {
			System.out.print(node.getName() + " : " + node.getCount() + "; ");
		}
		System.out.println();
	}

	
	public void testSortByFrequent1Itemset() {
		FPTree tree = new FPTree();
		int minSupport = 3;
		tree.setMinSupport(minSupport);
		List<List<String>> transactions =
			tree.readTransactionRecord("src\\artiano\\ml\\association\\test\\data2.txt");
		List<FPTreeNode> frequent1Itemset =
			tree.buildHeaderTable(transactions);
		for (List<String> transRecord : transactions) {
            LinkedList<String> record = 
            	tree.sortByFrequent1Itemset(transRecord, frequent1Itemset);
            for(String item : record) {
            	System.out.print(item + " ");
            }
            System.out.println();
		}
	}
	
	
	public void testBuildFPTree() {
		FPTree tree = new FPTree();
		int minSupport = 3;
		tree.setMinSupport(minSupport);
		List<List<String>> transactions =
			tree.readTransactionRecord("src\\artiano\\ml\\association\\test\\data2.txt");
		// 构建项头表，同时也是频繁1项集
        List<FPTreeNode> HeaderTable = tree.buildHeaderTable(transactions);
        // 构建FP-Tree
        FPTreeNode treeRoot = tree.buildFPTree(transactions, HeaderTable);
        Queue<FPTreeNode> nodeQueue = new LinkedList<FPTreeNode>();
        nodeQueue.add(treeRoot);
        while(!nodeQueue.isEmpty()) {
        	FPTreeNode node = nodeQueue.remove();
        	System.out.println(node.getName());
        	List<FPTreeNode> chidren = node.getChildren();
        	if(chidren == null) {
        		continue;
        	}
        	for(FPTreeNode child : chidren) {
        		nodeQueue.add(child);
        	}
         }
	}
	
	@org.junit.Test
	public void testFPTree() {
		FPTree fptree = new FPTree();
        fptree.setMinSupport(3);
        List<List<String>> transRecords = 
        	fptree.readTransactionRecord("src\\artiano\\ml\\association\\test\\data2.txt");
        fptree.FPGrowth(transRecords, null);
	}
}
