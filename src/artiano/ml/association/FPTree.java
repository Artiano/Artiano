package artiano.ml.association;

import java.io.*;
import java.util.*;

/**
 * <p>Description: FPTree for finding frequent k-item set.</p>
 * @author JohnF Nash
 * reference: http://blog.csdn.net/abcjennifer/article/details/7928082
 * @version 1.0.0
 * @date 2013-10-17
 * @function 
 * @since 1.0.0
 */
public class FPTree {
	
	private int minSupport;		
	
	public int getMinSupport() {
		return minSupport;
	}

	public void setMinSupport(int minSupport) {
		this.minSupport = minSupport;
	}

	public List<List<String>> readTransactionRecord(String fileName) {
		List<List<String>> transaction = 
			new ArrayList<List<String>>();
		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			String line;
			List<String> record = new ArrayList<String>();			
			while((line = br.readLine()) != null) {
				if(line.trim().length() > 0) {
					String[] str = line.split("[,��]");
					record = new LinkedList<String>();
					for(String w : str) {
						record.add(w.trim());
					}
					transaction.add(record);
				}
			}		
			br.close();
			
		} catch (IOException e) {
			System.out.println("Read transaction records failed."
                  + e.getMessage());
		}		
		return transaction;
	}
	
	//find frequent 1 item set
	public List<FPTreeNode> buildHeaderTable(List<List<String>> transactions) {
		if(transactions.size() == 0) {
			throw new IllegalArgumentException("Transaction empty.");
		}
		
		List<FPTreeNode> frequent1Itemset = new ArrayList<FPTreeNode>();
		Map<String, FPTreeNode> itemsetMap = new HashMap<String, FPTreeNode>();
		for(List<String> transaction: transactions) {
			for(String item : transaction) {
				if(!itemsetMap.containsKey(item)) {
					FPTreeNode node = new FPTreeNode(item);
					node.setCount(1);
					itemsetMap.put(item, node);
				} else {
					itemsetMap.get(item).countIncrement(1);
				}
			}
		} 
		
		// ��֧�ֶȴ��ڣ�����ڣ�minSup������뵽F1��
		Set<String> itemNames = itemsetMap.keySet();
		for (String name : itemNames) {
            FPTreeNode tnode = itemsetMap.get(name);
            if (tnode.getCount() >= minSupport) {
                frequent1Itemset.add(tnode);
            }
        }
		Collections.sort(frequent1Itemset);
		return frequent1Itemset;
	}
	
	// FP-Growth�㷨
    public void FPGrowth(List<List<String>> transRecords,
            List<String> postPattern) {        	
    	if(transRecords == null || transRecords.size() == 0) {
    		return;
    	}
    	// ������ͷ��ͬʱҲ��Ƶ��1�
        List<FPTreeNode> HeaderTable = buildHeaderTable(transRecords);
        // ����FP-Tree
        FPTreeNode treeRoot = buildFPTree(transRecords, HeaderTable);
        // ���FP-TreeΪ���򷵻�
        if (treeRoot.getChildren()==null || treeRoot.getChildren().size() == 0) {
            return;
        }
        
        //�����ͷ���ÿһ��+postPattern
        if(postPattern!=null){
            for (FPTreeNode header : HeaderTable) {
                System.out.print(header.getCount() + "\t" + header.getName());
                for (String ele : postPattern) {
                    System.out.print("\t" + ele);
                }
                System.out.println();
            }
        }
        
        // �ҵ���ͷ���ÿһ�������ģʽ��������ݹ����
        for (FPTreeNode header : HeaderTable) {
            // ��׺ģʽ����һ��
            List<String> newPostPattern = new LinkedList<String>();
            newPostPattern.add(header.getName());
            if (postPattern != null) {
                newPostPattern.addAll(postPattern);
            }
            // Ѱ��header������ģʽ��CPB������newTransRecords��
            List<List<String>> newTransRecords = new LinkedList<List<String>>();
            FPTreeNode backnode = header.getNextHomonym();
            while (backnode != null) {
                int counter = backnode.getCount();
                List<String> prenodes = new ArrayList<String>();
                FPTreeNode parent = backnode;
                // ����backnode�����Ƚڵ㣬�ŵ�prenodes��
                while ((parent = parent.getParent()).getName() != null) {
                    prenodes.add(parent.getName());
                }
                while (counter-- > 0) {
                    newTransRecords.add(prenodes);
                }
                backnode = backnode.getNextHomonym();
            }
            // �ݹ����
            FPGrowth(newTransRecords, newPostPattern);
        }
    }

	public FPTreeNode buildFPTree(List<List<String>> transRecords,
			List<FPTreeNode> frequent1Iteset) {
		FPTreeNode root = new FPTreeNode(); // �������ĸ��ڵ�
		for (List<String> transRecord : transRecords) {
            LinkedList<String> record = 
            	sortByFrequent1Itemset(transRecord, frequent1Iteset);
            FPTreeNode subTreeRoot = root;
            FPTreeNode tempRoot = null;
            if(root.getChildren() != null) {
            	while(!record.isEmpty() 
            		&& (tempRoot = subTreeRoot.findChild(record.peek())) != null ) {
            		//tempRoot = subTreeRoot.findChild(record.peek());
            		if(tempRoot != null) {
            			tempRoot.countIncrement(1);
            			subTreeRoot = tempRoot;
            			record.poll();
            		}
            	}
            }
            if(record.size() > 0) {
            	addNodes(subTreeRoot, record, frequent1Iteset);
            }                        
		}
		return root;
	}

	public LinkedList<String> sortByFrequent1Itemset(List<String> transRecord,
			List<FPTreeNode> headerTable) {
		LinkedList<String> sortedItemset = new LinkedList<String>();
		for(int i=0; i<headerTable.size(); i++) {
			FPTreeNode itemNode = headerTable.get(i);
			if(transRecord.contains(itemNode.getName())) {
				sortedItemset.add(itemNode.getName());
			}
		}
		return sortedItemset;
	}
	
	// ��record��Ϊancestor�ĺ����������
    public void addNodes(FPTreeNode ancestor, LinkedList<String> record,
    		List<FPTreeNode> F1) {
    	while(record.size() > 0){
    		String item = record.poll();
    		FPTreeNode child = new FPTreeNode(item);
    		child.setCount(1);    		
    		child.setParent(ancestor);        
    		ancestor.addChidren(child); 
    		
    		for (FPTreeNode f1 : F1) {
                if (f1.getName().equals(item)) {
                    while (f1.getNextHomonym() != null) {
                        f1 = f1.getNextHomonym();
                    }
                    f1.setNextHomonym(child);
                    break;
                }
            }
    		
    		addNodes(child, record, F1);
    	}
    }
}