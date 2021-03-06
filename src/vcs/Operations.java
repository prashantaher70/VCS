package vcs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
//import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.diff.core.Diff;
import com.diff.core.FileDiffResult;
import com.diff.core.MergeResult;
import com.diff.core.MergeResultItem;

import logger.VCSLogger;
import network.ConfigManipulation;
import network.FileUpload;
import network.ZipDirectory;
import objects.AbstractVCSTree;
import objects.VCSBlob;
import objects.VCSCommit;
import objects.VCSObject;
import objects.VCSTree;
import vcs.Constants;
//import network.HashUtil;
import java.lang.System;

public class Operations {

	public static String getPullTempFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + File.separator + Constants.VCSFOLDER
				+ File.separator + Constants.PullTemp_FOLDER);
		return toCreateDirPath.toString();
	}
	
	public static String getBranchesFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.BRANCH_FOLDER);
		return toCreateDirPath.toString();
	}

	public static String getHooksFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.HOOKS_FOLDER);
		return toCreateDirPath.toString();
	}

	public static String getInfoFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.INFO_FOLDER);
		return toCreateDirPath.toString();
	}

	public static String getLogsFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.LOGS_FOLDER);
		return toCreateDirPath.toString();
	}

	public static String getObjectsFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.OBJECTS_FOLDER);
		return toCreateDirPath.toString();
	}

	public static String getRefsFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.REFS_FOLDER);
		return toCreateDirPath.toString();
	}

	public static String getHeadsFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.REFS_FOLDER + "/" + Constants.HEADS_FOLDER);
		return toCreateDirPath.toString();
	}

	public static String getTagsFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.REFS_FOLDER + "/" + Constants.TAGS_FOLDER);
		return toCreateDirPath.toString();
	}

	public static boolean isVCSRepository(String workingDir) {
		return false;
	}

	
	/*public static String getConfigFolder(String workingDirectory) {
		Path toCreateDirPath=Paths.get(workingDirectory+Constants.VCSFOLDER +"/");
		return toCreateDirPath.toString();
	}*/
	
	public boolean initRepository(String workingDirectory){
		boolean returnStatus = false;
		Path workingDirPath=Paths.get(workingDirectory);
		if(Files.exists(workingDirPath))
		{
			Path vcsPath=Paths.get(workingDirectory+".vcs");
			if(Files.exists(vcsPath))
			{
				//repository is already initialized.
			}
			else
			{
				//CREATE or UPDATE repoListHolder
				String repoName = null;
				String sample = vcsPath.toString();
				if(sample.contains("\\"))
				{
					sample=sample.replace("\\", "/");
				}
				String delims = "[/]";
				String[] tokens = sample.split(delims);
				System.out.println("tokens.length "+(tokens.length - 2) +"    "+sample);
				repoName = tokens[tokens.length - 2];
				
				//Create a File or fetch it (if already created) from user.home
				String userHomeDir = System.getProperty("user.home");
				//VCSLogger.infoLogToCmd("User Home Directory: " + userHomeDir);

				//repoListHolder contains a List to all the local repositories initiated.
				File repoListHolder = new File(userHomeDir + "/repoListHolder.txt");
				
				try {
					
					//New File created otherwise else block is executed.
					boolean flag = repoListHolder.createNewFile();
					
					if(flag) {
						System.out.println("File repoListHolder created successfully.");
					}
					else {
						System.out.println("File repoListHolder already exists.");
					}
					
					//Writing to the File. FileWriter(File, boolean(append?) )
					try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(repoListHolder, true)))) {
					    out.println(repoName + ".vcs" + " " + workingDirectory); 
					    //Initially was vcsPath.toString();
					    // Format stored: repoName /Absolute/Path/to/the/repository/
					}
					catch (IOException e) {
						VCSLogger.infoLogToCmd("Data write to repoListHolder failed!");
					}
					
				} 
				catch (IOException e) {
					// TODO: handle exception
					VCSLogger.infoLogToCmd("Error in creating the File repoListHolder: " + e);
				} 
				
				//CREATE .VCS FOLDER
				boolean vcsFolderCreated=new File(vcsPath.toString()).mkdir();
				if(vcsFolderCreated)
				{
					getBranchesFolder(workingDirectory);
					boolean branchesFolderCreated = new File(
							Operations.getBranchesFolder(workingDirectory))
							.mkdir();

					getHooksFolder(workingDirectory);
					boolean hooksFolderCreated = new File(
							Operations.getHooksFolder(workingDirectory))
							.mkdir();

					getInfoFolder(workingDirectory);
					boolean infoFolderCreated = new File(
							Operations.getInfoFolder(workingDirectory)).mkdir();

					getLogsFolder(workingDirectory);
					boolean logsFolderCreated = new File(
							Operations.getLogsFolder(workingDirectory)).mkdir();

					getObjectsFolder(workingDirectory);
					boolean objectsFolderCreated = new File(
							Operations.getObjectsFolder(workingDirectory))
							.mkdir();

					getRefsFolder(workingDirectory);
					boolean refsFolderCreated = new File(
							Operations.getRefsFolder(workingDirectory)).mkdir();

					getHeadsFolder(workingDirectory);
					boolean headsFolderCreated = new File(
							Operations.getHeadsFolder(workingDirectory))
							.mkdir();

					getTagsFolder(workingDirectory);

					boolean tagsFolderCreated=new File(Operations.getTagsFolder(workingDirectory)).mkdir();
					
					//getConfigFolder(workingDirectory);
					boolean configCreated = false;
					try {
						configCreated = new File(workingDirectory + Constants.VCSFOLDER + "/config").createNewFile();
						writeConfig(workingDirectory);
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						VCSLogger.infoLogToCmd("UNABLE TO CREATE THE CONFIG FILE: " + e);
					}
					
					if(branchesFolderCreated && hooksFolderCreated && infoFolderCreated && logsFolderCreated && objectsFolderCreated && refsFolderCreated && headsFolderCreated && tagsFolderCreated && configCreated)
					{
						VCSLogger.infoLogToCmd("Bare repository initialised in " + workingDirectory);
						returnStatus = true;
					}
				}
			}
		}
		return returnStatus;
	}
	
	public boolean writeConfig(String workingDir) throws IOException
	{
		File config = new File(workingDir + Constants.VCSFOLDER + "/config");
		boolean status;
			
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(config, true)))) {
		    out.println("[core]\n");
		    //[remote 'origin'] url = repoUrl or http://127.0.0.1:8080/VCSD_1_1.vcs
		    status = true;
		}
		catch (IOException e) {
			status = false;
			VCSLogger.infoLogToCmd("DATA WRITES TO CONFIG FILE FAILED!");
		}
		return status;
	}	
	
	public VCSCommit getHead(String workingDir) throws IOException
	{
		BufferedReader br = null;
		File head = new File(getHeadsFolder(workingDir) + "/head");
		VCSCommit parent = null;
		String sCurrentLine;
		if (head.exists()) {
			br = new BufferedReader(new FileReader(getHeadsFolder(workingDir)
					+ "/head"));
			while ((sCurrentLine = br.readLine()) != null) {
				parent = new VCSCommit(sCurrentLine, workingDir,
						VCSCommit.IMPORT_TREE);
			}
			br.close();
		}
		return parent;
	}

	public boolean writeHead(String workingDir, String commitHash)
			throws IOException {
		{
			File head = new File(getHeadsFolder(workingDir) + "/head");
			FileWriter fileWritter = new FileWriter(head, false);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(commitHash);
			bufferWritter.close();
		}

		{
			System.out.println("reached here!!");
			File head = new File(getHeadsFolder(workingDir) + "/currentBranch");
			System.out.println("not reached here!!");
			FileWriter fileWritter = new FileWriter(head, false);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			System.out.println("Inside write head..hash = "+commitHash);
			VCSCommit commitObj = new VCSCommit(commitHash, workingDir, VCSCommit.IMPORT_JUST_COMMIT);
			bufferWritter.write(commitObj.getBranchName());
			bufferWritter.close();
		}
		
		return true;
	}	

	public String commit(String[] stagedFiles,VCSCommit parentCommit, String message,String author,String committer,String workingDir) throws IOException
	{
		VCSTree workDir = new VCSTree("workDir", workingDir, workingDir);
		workDir.setModified(true);
		VCSTree currentTree = workDir;
		VCSTree eleAtPath = null;
		VCSBlob lastEleAtPath = null;
		int[] insertedDeleted=null;
		int noOfLinesAdded=0,noOfLinesDeleted=0;
		for(int i=0;i<stagedFiles.length;i++)
		{
			currentTree = workDir;
			StringBuilder overallPath = new StringBuilder();
			String[] path = stagedFiles[i].split("/");
			overallPath.append(workingDir);
			VCSLogger.debugLogToCmd("overall path", overallPath.toString());
			//System.out.println("overall path" +overallPath);
				for(int j=0;j<path.length;j++)
				{
					if(j != 0)
					{
						overallPath.append("/");
					}
					overallPath.append(path[j]);
					if(j!=path.length-1)
					{
						AbstractVCSTree searchedItem=currentTree.getIfExist(path[j], "tree");
						if(searchedItem ==null)
						{
							eleAtPath = new VCSTree(path[j],overallPath.toString(), workingDir);
							eleAtPath.setModified(true);
							currentTree.addItem(eleAtPath);
							currentTree = eleAtPath;
						}
						else
						{
							currentTree = (VCSTree)searchedItem;
						}
					}
					else
					{
						AbstractVCSTree searchedItem=currentTree.getIfExist(path[j], "blob");
						if(searchedItem==null)
						{
							//System.out.println("NOT NULL FILE"+ path[j]);
							lastEleAtPath = new VCSBlob(path[j], overallPath.toString(), workingDir);
							//overall path 
							//check if the file exists in parent commit and do diff
							lastEleAtPath.setModified(true);
							currentTree.addItem(lastEleAtPath);
							//System.out.println("here " +eleAtPath.printTree());
						}
					}
				}
			
			insertedDeleted=doDiffWork(parentCommit, stagedFiles[i], workingDir);
			noOfLinesAdded+=insertedDeleted[0];
			noOfLinesDeleted+=insertedDeleted[1];
		}
		if(parentCommit!=null)
		{
			currentTree = workDir;
			VCSTree parentTree = parentCommit.getTree();
			VCSLogger.debugLogToCmd("Operations#commit#parentTree\n",parentTree.printTree(0));
			inorder(currentTree,parentTree,"./",workingDir);
		}
		
		VCSLogger.debugLogToCmd("Operations#commit#newTree\n",workDir.printTree(0));
		VCSCommit iniCommit = new VCSCommit(workingDir, parentCommit, workDir, message, author, committer);
		iniCommit.setNoOfLinesInserted(noOfLinesAdded);
		iniCommit.setNoOfLinesDeleted(noOfLinesDeleted);
		
		boolean done=writeToDeveloperList(committer,workingDir);
		
		String branchName=getCurrentBranchName(workingDir);
		try 
		{
			writeBranchHead(workingDir, iniCommit.getObjectHash(), branchName);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iniCommit.setBranchName(branchName);
		
		
		System.out.println("branchName= "+iniCommit.getBranchName());
		System.out.println("lines added " +noOfLinesAdded+" lines deleted "+noOfLinesDeleted);
		iniCommit.setCommitTimestamp(System.currentTimeMillis());
		iniCommit.writeCommitToDisk();
		System.out.println("commit hash	"+iniCommit.getObjectHash());
		VCSLogger.infoLogToCmd(iniCommit.getTree().printTree(0));
		return iniCommit.getObjectHash();
	}
	
	
	public int[] doDiffWork(VCSCommit parentCommit, String fileName, String workingDir)
	{
		int[] insertedDeleted=new int[2];
		if(parentCommit!=null && parentCommit.getTree()!=null)
		{
			AbstractVCSTree obj=parentCommit.getTree().findTreeIfExist(fileName, 0);
			VCSBlob b=(VCSBlob)obj;
			String fullFileName=null;
			if(b!=null)
			{
				//System.out.println(overallPath+"  parent commit's filename "+b.getObjectHash());
				fullFileName=b.writeTempFile(workingDir+"/"+Constants.VCSFOLDER+"/"+Constants.TEMP_FOLDER +"/",workingDir);
			}
			//do diff here
			if(fullFileName!=null)
			{
				//System.out.println(workingDir+"/"+stagedFiles[i]+" "+fullFileName);
				Diff diffObj=new Diff();
				FileDiffResult result=diffObj.diff(readFileIntoString(fullFileName),readFileIntoString(workingDir+"/"+fileName), null, false);
				insertedDeleted[1]+=result.getLineResult().getNoOfLinesDeleted();
				insertedDeleted[0]+=result.getLineResult().getNoOfLinesAdded();
				result=null;
				File f=new File(fullFileName);
				f.delete();
			}
			else
			{
				Diff diffObj=new Diff();
				FileDiffResult result=diffObj.diff("",readFileIntoString(workingDir+fileName), null, false);
				result.getLineResult().setNoOfLinesDeleted(result.getLineResult().getNoOfLinesDeleted()-1);
				insertedDeleted[1]+=result.getLineResult().getNoOfLinesDeleted();
				insertedDeleted[0]+=result.getLineResult().getNoOfLinesAdded();
				result=null;
			}
		}
		else if(parentCommit==null)
		{
			Diff diffObj=new Diff();
			
			FileDiffResult result=diffObj.diff("",readFileIntoString(workingDir+fileName), null, false);
			result.getLineResult().setNoOfLinesDeleted(result.getLineResult().getNoOfLinesDeleted()-1);
			//System.out.println("stagedfiles[i] "+stagedFiles[i] +" deleted "+result.getLineResult().getNoOfLinesDeleted());
			insertedDeleted[1]+=result.getLineResult().getNoOfLinesDeleted();
			insertedDeleted[0]+=result.getLineResult().getNoOfLinesAdded();
			result=null;
		}
		return insertedDeleted;
	}
	
	
	public void doVCSDiff(VCSCommit parentCommit, String fileName, String workingDir)
	{
		int[] insertedDeleted=new int[2];
		if(parentCommit!=null && parentCommit.getTree()!=null)
		{
			AbstractVCSTree obj=parentCommit.getTree().findTreeIfExist(fileName, 0);
			VCSBlob b=(VCSBlob)obj;
			String fullFileName=null;
			FileDiffResult result=null;
			if(b!=null)
			{
				//System.out.println(overallPath+"  parent commit's filename "+b.getObjectHash());
				fullFileName=b.writeTempFile(workingDir+"/"+Constants.VCSFOLDER+"/"+Constants.TEMP_FOLDER +"/",workingDir);
			}
			//do diff here
			String rightFile=readFileIntoString(workingDir+"/"+fileName);
			if(rightFile!=null)
			{
				if(fullFileName!=null)
				{
					//System.out.println(workingDir+"/"+stagedFiles[i]+" "+fullFileName);
					Diff diffObj=new Diff();
					result=diffObj.diff(readFileIntoString(fullFileName),rightFile, null, false);
					diffObj.viewResults(result, result.getLeftFile(),result.getRightFile());
					insertedDeleted[1]+=result.getLineResult().getNoOfLinesDeleted();
					insertedDeleted[0]+=result.getLineResult().getNoOfLinesAdded();
					File f=new File(fullFileName);
					f.delete();
				}
				else
				{
					Diff diffObj=new Diff();
					result=diffObj.diff("",rightFile, null, false);
					diffObj.viewResults(result, result.getLeftFile(),result.getRightFile());
					result.getLineResult().setNoOfLinesDeleted(result.getLineResult().getNoOfLinesDeleted()-1);
					insertedDeleted[1]+=result.getLineResult().getNoOfLinesDeleted();
					insertedDeleted[0]+=result.getLineResult().getNoOfLinesAdded();
				}
			}
			result=null;
		}
		else if(parentCommit==null)
		{
			Diff diffObj=new Diff();
			String rightFile=readFileIntoString(workingDir+fileName);
			if(rightFile!=null)
			{
				FileDiffResult result=diffObj.diff("",rightFile, null, false);
				result.getLineResult().setNoOfLinesDeleted(result.getLineResult().getNoOfLinesDeleted()-1);
				diffObj.viewResults(result, result.getLeftFile(),result.getRightFile());
				insertedDeleted[1]+=result.getLineResult().getNoOfLinesDeleted();
				insertedDeleted[0]+=result.getLineResult().getNoOfLinesAdded();
				result=null;
			}
		}
		VCSLogger.infoLogToCmd(insertedDeleted[0]+" lines added");
		VCSLogger.infoLogToCmd(insertedDeleted[1]+" lines deleted");
	}
	

	private boolean writeToDeveloperList(String committer, String workingDir) {
		// TODO Auto-generated method stub
		String devListFile= workingDir +"/" +Constants.VCSFOLDER +"/" +Constants.DEVELOPERS_FILE;
		boolean done=false;
		File f=new File(devListFile);
		if(!f.exists())
		{
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(f.exists())
		{
			try 
			{
				BufferedReader br =new BufferedReader(new FileReader(f));
				ArrayList<String> committerList =new ArrayList<String>();
				String line=null;
				while((line=br.readLine())!=null)
				{
					if(!committerList.contains(line))
					{
						committerList.add(line);
					}
				}
				br.close();
				if(!committerList.contains(committer))
				{
					committerList.add(committer);
				}
				BufferedWriter bw=new BufferedWriter(new FileWriter(f));
				if(f.exists())
				{
					int i=0,max=committerList.size();
					while(i<max)
					{
						if(i==0)
						{
							bw.write(committerList.get(i));
							if(i!=max-1)
							{
								bw.newLine();
							}
						}
						else
						{
							bw.append(committerList.get(i));
							if(i!=max-1)
							{
								bw.newLine();
							}
						}
						i++;
					}
				}
				bw.close();
			}
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return done;
	}

	private void inorder(AbstractVCSTree currentTree,
			AbstractVCSTree parentTree, String pathTillNow, String workingDir) {
		// TODO Auto-generated method stub

		ArrayList<AbstractVCSTree> childrenArray = ((VCSTree)currentTree).getImmediateChildren();
		ArrayList<AbstractVCSTree> parentArray = ((VCSTree)parentTree).getImmediateChildren();
		Iterator<AbstractVCSTree> childArrayIterator = childrenArray.iterator();
		Iterator<AbstractVCSTree> parentArrayIterator = parentArray.iterator();

		while (parentArrayIterator.hasNext()) {
			// AbstractVCSTree arg0 = childArrayIterator.next();
			AbstractVCSTree arg0 = parentArrayIterator.next();

			// VCSTree arg1 = (VCSTree) parentArrayIterator.next();

			// directory
			if (arg0.getType() == "tree") {
				// already not present
				VCSTree arg1 = (VCSTree) ((VCSTree) currentTree).getIfExist(
						arg0.getName(), "tree");
				if (arg1 == null) {
					// VCSTree eleAtPath = new
					// VCSTree(arg0.getName(),pathTillNow, workingDir);
					// ((VCSTree)currentTree).addItem(eleAtPath);
					// currentTree = eleAtPath;

					((VCSTree) currentTree).addItem(arg0);
					// currentTree = eleAtPath;

				} else {
					inorder(arg1, arg0, pathTillNow + "/" + arg1.getName(),
							workingDir);
				}

			} else if (arg0.getType() == "blob") // files
			{
				// already not present
				VCSBlob arg1 = (VCSBlob) ((VCSTree) currentTree).getIfExist(
						arg0.getName(), "blob");

				if (arg1 == null) {
					// VCSBlob lastEleAtPath = new VCSBlob(arg0.getName(),
					// pathTillNow, workingDir);
					((VCSTree) currentTree).addItem(arg0);
				}
			}
		}

	}

	public String getCurrentBranchName(String workingDir) throws IOException
	{
		File currentBranchFile =  new File(workingDir + ".vcs/refs/heads/currentBranch");
		if(currentBranchFile.exists())
		{
			InputStream in = new FileInputStream(currentBranchFile);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        StringBuilder out = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            out.append(line);
	        }
	        System.out.println(out.toString());   //Prints the string content read from input stream
	        reader.close();
			
	        return out.toString();
        }
		else
		{
			return "master";
		}
	}
	
	public static String readFileIntoString(String completeFileName) {
		String retVal = null;
		try {
			//System.out.println("complete file name" +completeFileName);
			retVal = new String(Files.readAllBytes(Paths.get(completeFileName)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}

	
	public VCSCommit getBranchHead(String workingDir,String branchName,int flag) throws IOException{
		BufferedReader br = null;
		File branch = new File(workingDir + ".vcs/branches/" + branchName);
		VCSCommit parent = null;
		String sCurrentLine;
		if(branch.exists()){
			br = new BufferedReader(new FileReader(workingDir + ".vcs/branches/" + branchName));
			while ((sCurrentLine = br.readLine()) != null) 
			{
				parent = new VCSCommit(sCurrentLine, workingDir, flag);
			}
			br.close();
		}
		return parent;
	}
	
	public VCSCommit getCommitTreeFromHead(String workingDir,String branchName) throws IOException{
		BufferedReader br = null;
		File branch = new File(workingDir + ".vcs/branches/" + branchName);
		VCSCommit parent = null;
		String sCurrentLine;
		if(branch.exists()){
			br = new BufferedReader(new FileReader(workingDir + ".vcs/branches/" + branchName));
			while ((sCurrentLine = br.readLine()) != null) 
			{
				parent = new VCSCommit(sCurrentLine, workingDir, VCSCommit.IMPORT_COMMITS);
			}
			br.close();
		}
		return parent;
	}
	
	public boolean writeBranchHead(String workingDir,String commitHash,String branchName) throws IOException{
		File branch = new File(workingDir + ".vcs/branches/"+branchName);
		FileWriter fileWritter = new FileWriter(branch,false);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	    bufferWritter.write(commitHash);
	    bufferWritter.close();
		return true;
	}
	
	boolean mergeBranch(String workingDirectory,String firstBranch,String secondBranch,String commonAncestorHash) throws IOException
	{
		boolean retval = false;
		String branchDirectory = workingDirectory + ".vcs/branches";
		System.out.println("inside mergeBranch");
		//if(branchExists(workingDirectory, firstBranch) && branchExists(workingDirectory, secondBranch))

		VCSCommit firstCommitObject = new VCSCommit(firstBranch, workingDirectory, VCSCommit.IMPORT_TREE);
		VCSCommit secondCommitObject  = new VCSCommit(secondBranch, workingDirectory, VCSCommit.IMPORT_TREE);
		
		if(commonAncestorHash==null)
		{
			commonAncestorHash = getCommonAncestor(workingDirectory,firstCommitObject.getBranchName(), secondCommitObject.getBranchName());
		}
		if(commonAncestorHash!=null)
		{
			//VCSCommit firstCommitObject = getBranchHead(workingDirectory, firstBranch);
			//VCSCommit secondCommitObject = getBranchHead(workingDirectory, secondBranch);
			
			
			VCSTree firstVCSTree =  firstCommitObject.getTree();
			VCSTree secondVCSTree =  secondCommitObject.getTree();
			VCSTree mergedVCSTree =  new VCSTree(firstVCSTree.getName(), firstVCSTree.getPath(), workingDirectory);
			
			if(firstVCSTree == null && secondVCSTree == null)
			{
				System.out.println("1st case");
				return false;
			}
			else if(firstVCSTree == null && secondVCSTree != null)
			{
				System.out.println("2th case");
				mergedVCSTree = secondVCSTree;
			}
			else if(firstVCSTree != null && secondVCSTree == null)
			{
				System.out.println("3th case");
				mergedVCSTree = firstVCSTree;
			}
			else
			{
				System.out.println("4th case");
				
				File branchOneTmpDir = new File(firstVCSTree.getWorkingDirectory()+".vcs/mergetmp/"+"branchOne/");
				branchOneTmpDir.mkdirs();
				File branchTwoTmpDir = new File(secondVCSTree.getWorkingDirectory()+".vcs/mergetmp/"+"branchTwo/");
				branchTwoTmpDir.mkdirs();
				File ancestorTmpDir = new File(secondVCSTree.getWorkingDirectory()+".vcs/mergetmp/"+"ancestor/");
				ancestorTmpDir.mkdirs();
				//String commonAncestorHash = getCommonAncestor(firstBranch,secondBranch);
				VCSCommit commonAncestorCommit = new VCSCommit(commonAncestorHash, workingDirectory, VCSCommit.IMPORT_TREE);
				VCSTree commonAncestor = commonAncestorCommit.getTree();
				mergeTree(firstVCSTree,secondVCSTree,mergedVCSTree,commonAncestor);
			}
			System.out.println(mergedVCSTree.printTree(0));
			//write mergedVCSTree to disk
			mergedVCSTree.writeOriginalToDisk();
			//prepare commit object
			VCSCommit mergedCommitObject = new VCSCommit(workingDirectory, firstCommitObject, mergedVCSTree, "merge", VCS.getUserName(), VCS.getUserName());
			mergedCommitObject.addParent(secondCommitObject);
			
			mergedCommitObject.setCommitTimestamp(System.currentTimeMillis());
			mergedCommitObject.setBranchName(firstCommitObject.getBranchName());
			//int[] insertedDeleted=null;
			//insertedDeleted = doDiffWork(firstCommitObject, fileName, workingDir);
			
			mergedCommitObject.reHashContent();
			System.out.println();
			System.out.println("commit hash is:"+mergedCommitObject.getObjectHash());
			mergedCommitObject.writeCommitToDisk();
			writeBranchHead(workingDirectory, mergedCommitObject.getObjectHash(), firstCommitObject.getBranchName());
			writeHead(workingDirectory, mergedCommitObject.getObjectHash());
			mergedVCSTree.writeOriginalToDisk();
			
		}
		return retval;
	}

	
	public static String getCommonAncestor(String workingDirectory, String branch1, String branch2)
	{
		boolean found = false;
		ArrayList<ArrayList<String>> relationList = caDiskToMemory(workingDirectory);
		ArrayList<String> individualRelation = null;
		
		
		//System.out.println("relation size is :"+relationList.size());
		//int individualRelationSize = 3;
		
		while(!found)
		{
			//System.out.println("here");
			for(int i=0;i<relationList.size();i++)
			{
				ArrayList<String> arg0 = relationList.get(i);
				if(arg0.get(0).equals(branch1) && arg0.get(1).equals(branch2))
				{
					found = true;
					//take appropriate action(bring the commit object into memory)
					return arg0.get(2);
				}
				else if(arg0.get(0).equals(branch2) && arg0.get(1).equals(branch1))
				{
					found = true;
					//same action as above
					return arg0.get(2);
				}
				for(int j=i+1;j<relationList.size() && !found ;j++)
				{
					ArrayList<String> arg1 = relationList.get(j);
					if(arg0.get(0).equals(arg1.get(0)))
					{
						if(arg0.get(1).equals(branch1) && arg1.get(1).equals(branch2))
						{
							arg0.set(0, branch2);
							arg0.set(2,arg1.get(2));
							relationList.remove(j);
							found = true;
						}
						else if(arg0.get(1).equals(branch2) && arg1.get(1).equals(branch1))
						{
							arg0.set(0, branch1);
							arg0.set(2,arg1.get(2));
							relationList.remove(j);
							found = true;
						}
						
					}
					else if(arg0.get(1).equals(arg1.get(1)))
					{
						if(arg0.get(1).equals(branch1) && arg1.get(0).equals(branch2))
						{
							arg0.set(1, branch2);
							arg0.set(2,arg1.get(2));
							relationList.remove(j);
							found = true;
						}
						else if(arg0.get(0).equals(branch2) && arg1.get(0).equals(branch1))
						{
							arg0.set(1, branch1);
							arg0.set(2,arg1.get(2));
							relationList.remove(j);
							found = true;
						}
						
					}
					else if(arg0.get(0).equals(arg1.get(1)))
					{
						if(arg0.get(1).equals(branch1) && arg1.get(0).equals(branch2))
						{
							arg0.set(0, branch2);
							arg0.set(2,arg1.get(2));
							relationList.remove(j);
							found = true;
						}
						else if(arg0.get(1).equals(branch2) && arg1.get(0).equals(branch1))
						{
							arg0.set(0, branch1);
							arg0.set(2,arg1.get(2));
							relationList.remove(j);
							found = true;
						}
					}
					else if(arg0.get(1).equals(arg1.get(0)))
					{
						System.out.println("inside else if");
						if(arg0.get(0).equals(branch1) && arg1.get(1).equals(branch2))
						{
							arg0.set(1, branch2);
							arg0.set(2,arg1.get(2));
							relationList.remove(j);
							found = true;
						}
						else if(arg0.get(0).equals(branch2) && arg1.get(1).equals(branch1))
						{
							arg0.set(1, branch1);
							arg0.set(2,arg1.get(2));
							relationList.remove(j);
							found = true;
						}
					}
				}
				found = false;
			}
			
		}
		
		return null;
	}
	public static ArrayList<ArrayList<String>> caDiskToMemory(String workingDirectory)
	{
		String completeFile = readFileIntoString(workingDirectory+".vcs/commonAncestor");
		//System.out.println(completeFile);
		String textStr[] = completeFile.split("\\r?\\n");
		//System.out.println("splitted file:");
		/*
		for(int i=0;i<textStr.length;i++)
		{
			System.out.println(textStr[i]);
			System.out.println();
		}
		*/
		ArrayList<String> tempRelationList = new ArrayList<String>(Arrays.asList(textStr));
		int relationListSize = tempRelationList.size();
		
		ArrayList<ArrayList<String>> relationList = new ArrayList<ArrayList<String>>();
		
		for(int i=0;i<relationListSize;i++)
		{
			String individualRelation = tempRelationList.get(i);
			String[] splited = individualRelation.split("\\s+");
			System.out.println("splitted individual relation:");
			for(int j=0;j<textStr.length;j++)
			{
				System.out.println(splited[j]);
				System.out.println();
			}
			ArrayList<String> individualRelationArray = new ArrayList<String>(Arrays.asList(splited));
			relationList.add(individualRelationArray);
		}
		
		return relationList;
		
	}
	
	//WAIT
	public void mergePullBranch() {
		
	}
	
	void mergeTree(VCSTree firstVCSTree,VCSTree secondVCSTree,VCSTree mergedVCSTree, VCSTree commonAncestor) throws IOException
	{
		System.out.println("mergeTree entered");
		ArrayList<AbstractVCSTree> firstVCSTreeChildren = firstVCSTree.getImmediateChildren();
		ArrayList<AbstractVCSTree> secondVCSTreeChildren = secondVCSTree.getImmediateChildren();
		int firstVCSTreeChildrenLength = firstVCSTreeChildren.size();
		
		mergedVCSTree.setModified(true);
		
		int firstCounter=0;
		int secondCounter=0;
		Iterator<AbstractVCSTree> firstVCSTreeIterator = firstVCSTreeChildren.iterator();
		while(firstVCSTreeIterator.hasNext())
		{
			AbstractVCSTree firstTmpObj = firstVCSTreeIterator.next();
			AbstractVCSTree secondTmpObj = secondVCSTree.getIfExist(firstTmpObj.getName(), firstTmpObj.getType());
			//common trees(directories)
			if(secondTmpObj!=null && secondTmpObj.getType().equals("tree"))
			{
				VCSTree mergedVCSTreeChild = new VCSTree(firstTmpObj.getName(),firstTmpObj.getPath(),firstTmpObj.getWorkingDirectory());
				mergedVCSTreeChild.setModified(true);
				mergedVCSTree.addItem(mergedVCSTreeChild);
				mergeTree((VCSTree)firstTmpObj,(VCSTree)secondTmpObj,mergedVCSTreeChild,commonAncestor);
				firstVCSTreeIterator.remove();
				secondVCSTreeChildren.remove(secondTmpObj);
			}
			//common files
			else if(secondTmpObj!=null && secondTmpObj.getType().equals("blob"))
			{
				//same hash => no change
				if(firstTmpObj.getObjectHash().equals(secondTmpObj.getObjectHash()))
				{
					firstTmpObj.setModified(true);
					mergedVCSTree.addItem(firstTmpObj);
					
				}
				//changed files
				else
				{
					System.out.println("Rao's merge entered");
					//mergedVCSTree.addItem(firstTmpObj);///delete after test..no use
					//call rao's function to merge both files
					
								((VCSBlob)firstTmpObj).writeOriginalToTempDir(firstTmpObj.getWorkingDirectory() + ".vcs/mergetmp/branchOne/" + firstTmpObj.getName());
								((VCSBlob)secondTmpObj).writeOriginalToTempDir(secondTmpObj.getWorkingDirectory() + ".vcs/mergetmp/branchTwo/" + secondTmpObj.getName());
								//((VCSBlob)firstTmpObj).writeOriginalToTempDir(firstTmpObj.getWorkingDirectory() + firstTmpObj.getRelativePath());
								
								
								String file1Contents = readFileIntoString(firstTmpObj.getWorkingDirectory() + ".vcs/mergetmp/branchOne/" + firstTmpObj.getName());
								String file2Contents = readFileIntoString(secondTmpObj.getWorkingDirectory() + ".vcs/mergetmp/branchTwo/" + secondTmpObj.getName());
								
								//String completeFileName = searchFileInTree(commonAncestor,firstTmpObj.getName());
								System.out.println("relative path is :  "+firstTmpObj.getRelativePath());
								String tmpRelativePath = firstTmpObj.getRelativePath();
								if(tmpRelativePath.charAt(0)=='/')
								{
									tmpRelativePath = tmpRelativePath.replaceFirst("/", "");
								}
								System.out.println("relative path now is :  "+tmpRelativePath);
								AbstractVCSTree completeFileNameObj = commonAncestor.findTreeIfExist(tmpRelativePath, 0);
								if(completeFileNameObj==null)
								{
									System.out.println("abs tree is null");
								}
								((VCSBlob)completeFileNameObj).writeOriginalToTempDir(firstTmpObj.getWorkingDirectory() + ".vcs/mergetmp/ancestor/" + firstTmpObj.getName());
								String file3Contents = readFileIntoString(firstTmpObj.getWorkingDirectory() + ".vcs/mergetmp/ancestor/" + firstTmpObj.getName());
								
								Diff diff = new Diff();
								//MergeResult mr=diff.merge(file3Contents, file1Contents, file2Contents, null, false);
								//no commit objects available ===> no branch names and no commit hashes available. So Tree hash has been used
								String mergedString = getMergeResult(firstTmpObj.getWorkingDirectory(),file3Contents, file1Contents, file2Contents, "","", firstTmpObj.getObjectHash(), secondTmpObj.getObjectHash());
								
								System.out.println("===============================================================");
								System.out.println(file1Contents);
								System.out.println();
								System.out.println(file2Contents);
								System.out.println();
								System.out.println(file3Contents);
								System.out.println();
								System.out.println(mergedString);
								System.out.println("===============================================================");
								
								
								//write this to blob
								//File mergedFile = new File(firstTmpObj.getPath());
								
								String fileDir = firstTmpObj.getWorkingDirectory()+".vcs/mergetmp/"+firstTmpObj.getName();
								System.out.println("relative path is :"+fileDir);
								File mergedFile = new File(fileDir);
						    	FileWriter fw = new FileWriter(mergedFile.getAbsoluteFile());
								BufferedWriter bw = new BufferedWriter(fw);
								bw.write(mergedString);
								bw.close();

								
								VCSBlob mergedFileBlob = new VCSBlob(firstTmpObj.getName(), firstTmpObj.getPath(), firstTmpObj.getWorkingDirectory());
								//add the contents of "mr.getDefaultMergedResult()" to 'mergedFile'
								mergedFileBlob.setModified(true);
								mergedFileBlob.setContentPath(fileDir);
								mergedVCSTree.addItem(mergedFileBlob);
								//mr.getDefaultMergedResult();
								
								
				}
				secondVCSTreeChildren.remove(secondTmpObj);
			}
			//non commmon files and directories
			else
			{
				firstTmpObj.setModified(true);
				mergedVCSTree.addItem(firstTmpObj);
			}
		}
		//non common files in second branch
		Iterator<AbstractVCSTree> secondVCSTreeIterator = secondVCSTreeChildren.iterator();
		while(secondVCSTreeIterator.hasNext())
		{
			AbstractVCSTree secondTmpObj = secondVCSTreeIterator.next();
			secondTmpObj.setModified(true);
			mergedVCSTree.addItem(secondTmpObj);
		}
	}

	public static String getMergeResult(String workingDirectory,String commonAncestor,String version1, String version2,String leftBranchName, String rightBranchName,String leftCommitId,String rightCommitId) throws IOException
	{
		String retVal=null;
		Diff obj = new Diff();
		MergeResult mr = obj.merge(commonAncestor, version1, version2,null, false);
		if (!mr.isConflict()) 
		{
			retVal=mr.getDefaultMergedResult();
		}
		else
		{
			//put lock on operations
			String branchDir = workingDirectory + ".vcs/conflict_file";
			File conflict_file = new File(branchDir);
	    	FileWriter fw = new FileWriter(conflict_file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("1");
			bw.close();

			StringBuilder sb=new StringBuilder();
			int i = 0;
			while (i < mr.getMergeItems().size()) 
			{
				MergeResultItem mri = mr.getMergeItems().get(i);
				if (mri.getType() == MergeResultItem.Type.CONFLICT)
				{
					int j = 0;
					sb.append("====================================");
					sb.append(leftBranchName);
					sb.append("          ");
					sb.append(leftCommitId);
					sb.append(System.lineSeparator());
					
					while (j < mri.getLeftVersion().size()) 
					{
						sb.append(mri.getLeftVersion().get(j).getContent());
						sb.append(System.lineSeparator());
						j++;
					}
					
					sb.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
					sb.append(rightBranchName);
					sb.append("          ");
					sb.append(rightCommitId);
					sb.append(System.lineSeparator());
					j = 0;
					while (j < mri.getLeftVersion().size()) 
					{
						sb.append(mri.getRightVersion().get(j).getContent());
						sb.append(System.lineSeparator());
						j++;
					}
					sb.append("------------------------------------");
				}
				else
				{
					int k=0,max;
					max=mri.getLeftVersion().size();
					while(k<max)
					{
						sb.append(mri.getLeftVersion().get(k));
						sb.append(System.lineSeparator());
						k++;
					}
				}
				i++;
			}
			retVal=sb.toString();
		 }
		return retVal;
	}

	
	
	boolean branchExists(String workingDirectory,String branchName)
	{
		boolean retval = false;
		System.out.println("workdir and branch name		"+workingDirectory+"   "+branchName);
		String branchDir = workingDirectory + ".vcs/branches";
		File f = new File(branchDir);
	    ArrayList<String> files = null;
		listAllFiles(workingDirectory, files, workingDirectory);
	    int filesArraySize = files.size();
	    System.out.println("files size : "+filesArraySize);
	    //System.out.println(files[0]);
	    //System.out.println(files[1]);
	    int flag = 1;
	    Iterator<String> iterator = files.iterator();
		while (iterator.hasNext()) {
			if(branchName.equals(iterator.next()))
			{
				retval = true;
			}
		}
	    /*
	    for(int i=0;i<filesArraySize && !retval;i++)
	    {
	    	if(branchName.equals(files[i]))
	    	{
	    		retval = true;
	    	}
	    }*/
		return retval;
	}

	boolean switchBranch(String nameOfBranch,String workingDirectory) throws IOException
	{
		boolean retval = true;
		//String workingDirectory = "/home/rounak/final year project/VCS v1.5.0/VCSDebug/";
		String branchDir = workingDirectory + ".vcs/branches";
		File f = new File(branchDir);
	    File[] files  = f.listFiles();
	    int filesArraySize = files.length;
	    int flag = 1;
	    for(int i=0;i<filesArraySize && flag==1;i++)
	    {
	    	if(nameOfBranch.equals(files[i]))
	    	{
	    		flag = 0;
	    	}
	    }
	    if(flag == 0)
	    {
	    	System.out.println("No such branch exists !");
	    }
	    else
	    {
	    	VCSCommit VCSCommitObj = getBranchHead(workingDirectory, nameOfBranch,VCSCommit.IMPORT_TREE);
	    	writeHead(workingDirectory, VCSCommitObj.getObjectHash());
	    	VCSCommitObj.getTree().writeOriginalToDisk();
	    	
			File head = new File(getHeadsFolder(workingDirectory) + "/currentBranch");
			FileWriter fileWritter = new FileWriter(head, false);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			VCSCommit commitObj = new VCSCommit(VCSCommitObj.getObjectHash(), workingDirectory, VCSCommit.IMPORT_JUST_COMMIT);
			bufferWritter.write(nameOfBranch);
			bufferWritter.close();
	    }

		return retval;
	}
	boolean createBranch(String nameOfBranch,String workingDirectory) throws IOException
	{
		//String workingDirectory = "/home/rounak/final year project/VCS v1.5.0/VCSDebug/";
		//String branchDir = "/home/rounak/final year project/VCS v1.5.0/VCSDebug/.vcs/branches";
		String branchDir = workingDirectory + ".vcs/branches";
		
		VCSCommit headCommit = getHead(workingDirectory);
		String commitHash = headCommit.getObjectHash();
		
		System.out.println("create branch dir "+branchDir);
		
		File f = new File(branchDir);
	    File[] files  = f.listFiles();
	    int flag = 1;
	    boolean retval = false;
		
	    
	    File branchFile = null;
	    //if(files.length==0 && nameOfBranch.equals("Master"))
	    int filesArraySize = files.length;
	    System.out.println("number of branches : "+filesArraySize);
	    if(filesArraySize>=1 && (nameOfBranch.equals("Master") || nameOfBranch.equals("master")))
	    {
	    	flag = 0;
	    }
	    for(int i=0;i<filesArraySize && flag==1;i++)
	    {
	    	if(nameOfBranch.equals(files[i]))
	    	{
	    		flag = 0;
	    	}
	    }
	    if(flag==1)
	    {
	    	branchFile = new File(branchDir + "/" +nameOfBranch);
	    	
	    	FileWriter fw = new FileWriter(branchFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(commitHash);
			bw.close();
			retval = true;
	    }
	    if(retval)
	    {
	    	writeHead(workingDirectory, commitHash);
	    }
	    VCSCommit caCommitObject = new VCSCommit(commitHash, workingDirectory, VCSCommit.IMPORT_JUST_COMMIT);
	    //String data = nameOfBranch + " " + caCommitObject.getBranchName() + " " + commitHash ;
	    String data = null;
		File file =new File(workingDirectory + ".vcs/commonAncestor");

		//if file doesnt exists, then create it
		boolean done = false;
		if(!file.exists()){
			done = file.createNewFile();
		}

		if(!done)
		{
			data = "\n" + nameOfBranch + " " + caCommitObject.getBranchName() + " " + commitHash ;
		}
		else
		{
			data = nameOfBranch + " " + caCommitObject.getBranchName() + " " + commitHash ;
		}
		
		//true = append file
		FileWriter fileWritter = new FileWriter(file,true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write(data);
	        bufferWritter.close();

        System.out.println("Done");
	    return retval;
	}
	
	public void branch(String workingDirectory) throws IOException
	{
		BufferedReader br = null;
		String sCurrentBranch = null;
		File currentBranch = new File(getHeadsFolder(workingDirectory) + "/currentBranch");
		String sCurrentLine;
		if (currentBranch.exists()) {
			br = new BufferedReader(new FileReader(getHeadsFolder(workingDirectory)+ "/currentBranch"));
			while ((sCurrentLine = br.readLine()) != null) {
				sCurrentBranch = sCurrentLine;
			}
			br.close();
		}

		
		ArrayList<String> files = new ArrayList<String>();
		listAllFiles(workingDirectory+".vcs/branches/", files, workingDirectory+".vcs/branches/");
		for(int i=0;i<files.size();i++)
		{
			if(files.get(i).equals(sCurrentBranch))
			{
				System.out.println(files.get(i)+"*");
			}
			else
			{
					System.out.println(files.get(i));
			}
		}
		
	}
	public void clean(String workingDir)
	{
		VCSCommit commit;
		try {
			commit = getHead(workingDir);
			boolean status = true;
			Iterator<AbstractVCSTree> it = commit.getTree().getImmediateChildren().listIterator();
			VCSLogger.debugLogToCmd("VCS#MAIN#checkout",commit.getTree().printTree(0));
			VCSLogger.debugLogToCmd("VCS#MAIN#checkout", "Tree Printed");
			while(it.hasNext())
			{
				status = (it.next()).writeOriginalToDisk();
				if(!status) break;
			}
			if(status) VCSLogger.infoLogToCmd("Successfully Cleaned");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void listAllFiles(String directoryName, ArrayList<String> files,String initialWorkingDir) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) 
	    {
	        if (file.isFile()) 
	        {
	        	//System.out.println("dirName " +directoryName +" iniworkingDir "+initialWorkingDir);
	        	String tempAbsPath=file.getAbsolutePath();
	        	if(tempAbsPath.contains("\\"))
	        	{
	        		tempAbsPath=tempAbsPath.replace("\\", "/");
	        	}
	        	//System.out.println("tempabspath " + tempAbsPath);
	        	String temp=tempAbsPath.replace(initialWorkingDir,"");
	        	//System.out.println("temp is "+temp);
				files.add(temp);
	        }
	        else if (file.isDirectory() && !(file.getName().startsWith("."))) 
	        {
	        	listAllFiles(file.getAbsolutePath(), files,initialWorkingDir);
	        }
	    }
	}
	
	
	
/*	public boolean writeCloneConfig(String workingDir, String repoUrl) throws IOException
	{
		File config = new File(workingDir + Constants.VCSFOLDER + "/config");
		boolean status;
			
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(config, true)))) {
		    out.println("[remote 'origin']\n\turl = " + repoUrl);
		    //[remote 'origin'] url = repoUrl or http://127.0.0.1:8080/VCSD_1_1.vcs
		    status = true;
		}
		catch (IOException e) {
			status = false;
			VCSLogger.infoLogToCmd("DATA WRITES TO CONFIG FILE FAILED!");
		}
		return status;
	}	
	*/
	public String clone(String repoUrl,String workDir){
		URLConnection conn;
		/** 
		 * NanoHttpd serve() is automatically called 
		 * and the response is stored in the InputStream variable response.
		 * 
		 * 1.) Now Unzip the .zip file received from the server and  
		 * generate a local directory for the same with an updated config file. and extra updates (?Check)
		 * 2.) On success, we add the remote repoUrl in config file with handle `origin`
		 * config:
		 * [remote 'origin'] url = repoUrl or http://127.0.0.1:8080/VCSD_1_1.vcs
		 */

		try {
			conn = new URL(repoUrl + "?REQUEST=CLONE").openConnection();
			//conn.setRequestProperty("Accept-Charset", "UTF-8"); *Not required
			conn.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
			conn.setRequestProperty("Accept","*/*");
			VCSLogger.infoLogToCmd("REPO URL: " + repoUrl);
			
			//int status = ((HttpURLConnection) conn).getResponseCode();
			//VCSLogger.infoLogToCmd("STATUS RECEIVED: " + status);
			
			String fileNameWithExtn = repoUrl.substring( repoUrl.lastIndexOf('/')+1, repoUrl.length() );
			String fileNameWithoutExtn = fileNameWithExtn.substring(0, fileNameWithExtn.lastIndexOf('.'));			

			String userWorkDir = System.getProperty("user.dir");
			//String userHomeDir = System.getProperty("user.home");
			
			//Receives Response from server (Check SimpleWebServer.java and stores in response InputStream)
			InputStream response = conn.getInputStream();
		
			// Code to see the content received from the server side.
			//New file created from the received response.
			FileOutputStream outStream = new FileOutputStream(new File(userWorkDir + File.separator + fileNameWithoutExtn + ".zip"));
			
			byte buf[] = new byte[8192];
			while(response.read(buf) > 0){
				outStream.write(buf);
			}
			outStream.close();
			response.close();
			
			// ~UNZIP CODE
			String filename = userWorkDir + File.separator + fileNameWithoutExtn + ".zip";			
			VCSLogger.infoLogToCmd("Unzipping...\nFILE:  " + filename);
			
			File srcFile = new File(filename);
			
			// create a directory with the same name to which the contents will be extracted
			String zipPath = filename.substring(0, filename.length()-4);
			File temp = new File(zipPath);
			temp.mkdir();
			
			ZipFile zipFile = null;
			
			try {
				
				zipFile = new ZipFile(srcFile);
				
				// get an enumeration of the ZIP file entries
				Enumeration<?> e = zipFile.entries();
				
				while (e.hasMoreElements()) {
					
					ZipEntry entry = (ZipEntry) e.nextElement();
					
					File destinationPath = new File(zipPath, entry.getName());
					 
					//create parent directories
					destinationPath.getParentFile().mkdirs();
					
					// if the entry is a file extract it
					if (entry.isDirectory()) {
						continue;
					}
					else {
						
						System.out.println("Extracting file: " + destinationPath);
						
						BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

						int b;
						byte buffer[] = new byte[1024];

						FileOutputStream fos = new FileOutputStream(destinationPath);
						
						BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);

						while ((b = bis.read(buffer, 0, 1024)) != -1) {
							bos.write(buffer, 0, b);
						}
						
						bos.close();
						bis.close();
						
					}
					
				}
				
			}
			catch (IOException ioe) {
				System.out.println("Error opening zip file: " + ioe);
			}
			finally 
			{
				try 
				{
					if (zipFile!=null) {
						zipFile.close();
					}
				}
				catch (IOException ioe) {
					System.out.println("Error while closing zip file" + ioe);
				}
			}
			
			/**
			 * Once the directory is cloned, update the config file and add any directories not zipped (empty)
			 * i.e. create the dirs. in .vcs folder
			 */
			
			String confPath = userWorkDir + File.separator + fileNameWithoutExtn + File.separator
					+ Constants.VCSFOLDER + "/config";
			ConfigManipulation cmClone = new ConfigManipulation(confPath);
			cmClone.writeCloneConfig(repoUrl);
			
			/*getBranchesFolder(workingDirectory);
			boolean branchesFolderCreated = new File(
					Operations.getBranchesFolder(workingDirectory))
					.mkdir();

			getHooksFolder(workingDirectory);
			boolean hooksFolderCreated = new File(
					Operations.getHooksFolder(workingDirectory))
					.mkdir();

			getInfoFolder(workingDirectory);
			boolean infoFolderCreated = new File(
					Operations.getInfoFolder(workingDirectory)).mkdir();

			getLogsFolder(workingDirectory);
			boolean logsFolderCreated = new File(
					Operations.getLogsFolder(workingDirectory)).mkdir();

			getObjectsFolder(workingDirectory);
			boolean objectsFolderCreated = new File(
					Operations.getObjectsFolder(workingDirectory))
					.mkdir();

			getRefsFolder(workingDirectory);
			boolean refsFolderCreated = new File(
					Operations.getRefsFolder(workingDirectory)).mkdir();

			getHeadsFolder(workingDirectory);
			boolean headsFolderCreated = new File(
					Operations.getHeadsFolder(workingDirectory))
					.mkdir();

			getTagsFolder(workingDirectory);
			boolean tagsFolderCreated=new File(Operations.getTagsFolder(workingDirectory)).mkdir();
			
			if(branchesFolderCreated && hooksFolderCreated && infoFolderCreated && logsFolderCreated && objectsFolderCreated && refsFolderCreated && headsFolderCreated && tagsFolderCreated && configCreated)
			{
				VCSLogger.infoLogToCmd("Repository successfully cloned in " + userWorkDir);
			}
*/			
			
		} // ~UNZIP CODE 
		
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * read commit tree, starting with current branch and returns a flattened and sorted arraylist based on timestamps
	 * @param workingDir
	 * @return
	 */
	public List<VCSCommit> readAndFlattenCommitTreeToList(String workingDir){
		List<VCSCommit> flattenedCommitTree = new ArrayList<VCSCommit>();
		Queue<VCSCommit> q=new LinkedList<VCSCommit>();
		String currentBranch = null;
		try {
			currentBranch = getCurrentBranchName(workingDir);
			VCSCommit branchHead = getBranchHead(workingDir, currentBranch,VCSCommit.IMPORT_COMMITS);
			// not on any branch, so unconditional return
			if (branchHead == null)
				return null;
			q.add(branchHead);

			VCSCommit qElement = null;
			ArrayList<VCSCommit> qElementAncestors = null;
			while (!q.isEmpty()) {
				qElement = q.remove();
				flattenedCommitTree.add(qElement);
				qElementAncestors = qElement.getParentCommits();
				for (VCSCommit commit : qElementAncestors) {
					q.add(commit);
				}
			}

			Collections.sort(flattenedCommitTree, new Comparator<VCSCommit>() {

				@Override
				public int compare(VCSCommit o1, VCSCommit o2) {
					return (o1.getCommitTimestamp() < o2.getCommitTimestamp()) ? 1
							: -1;
				}
			});
		} catch (IOException e) {

		}

		return flattenedCommitTree;
	}
	
	public void vcsCommitLog(String workingDir){
		List<VCSCommit> commitList = readAndFlattenCommitTreeToList(workingDir);
		StringBuilder builder = new StringBuilder();
		for(VCSCommit commit : commitList){
			//commit hash
			builder.append("Commit ");
			builder.append(commit.getObjectHash());
			builder.append("\n\t");
			//\t author
			builder.append("Author: ");
			builder.append(commit.getAuthor());
			
			builder.append("\n\t");
			//\tmeasssage
			builder.append("Message: ");
			builder.append(commit.getCommitMessage());
			builder.append("\n\t");
			
			//\t timestamp
			Date commitDate = new Date(commit.getCommitTimestamp());
			builder.append(commitDate);
			builder.append("\n\t");
			
			//\tbranch
			builder.append("Branch: ");
			builder.append(commit.getBranchName());
			builder.append("\n\t");
			
			builder.append("\n");
		}
		
		VCSLogger.infoLogToCmd(builder.toString());
	}
	
	public String pull(String remoteHandleName) {
		
		String userWorkDir = System.getProperty("user.dir");
		//String userHomeDir = System.getProperty("user.home");

		String confPath = userWorkDir + File.separator + Constants.VCSFOLDER + "/config";
		//File conf = new File(confPath);
		
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(confPath), Charset.defaultCharset());
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			VCSLogger.infoLogToCmd("Config File not found: " + e);
		}
		
		for (String line : lines) {
    		if (line.contains("remote "+remoteHandleName)) {
    			int i = lines.indexOf(line);
    			i++;
    			String urlLine = lines.get(i);
    			urlLine.replaceAll("/s+", "");
    			VCSLogger.infoLogToCmd("URL LINE: " + urlLine);
    			String urlParts[] = urlLine.split("=");
    			String repoUrl = urlParts[1];
    			
    			URLConnection conn;
    			try {
    				conn = new URL(repoUrl + "?REQUEST=PULL").openConnection();
    				//conn.setRequestProperty("Accept-Charset", "UTF-8"); *Not required
    				conn.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
    				conn.setRequestProperty("Accept","*/*");
    				VCSLogger.debugLogToCmd("URL", "REPO URL: " + repoUrl);
    				
    				//int status = ((HttpURLConnection) conn).getResponseCode();
    				//VCSLogger.infoLogToCmd("STATUS RECEIVED: " + status);
    				
    				String fileNameWithExtn = repoUrl.substring( repoUrl.lastIndexOf('/')+1, repoUrl.length() );
    				String fileNameWithoutExtn = fileNameWithExtn.substring(0, fileNameWithExtn.lastIndexOf('.'));			

    				InputStream response = conn.getInputStream();
    				
    				getPullTempFolder(userWorkDir);
    				boolean pullTempCreated = new File(Operations.getPullTempFolder(userWorkDir)).mkdir();
    				if(pullTempCreated) {
    					//VCSLogger.infoLogToCmd("> Creatd pulltemp directory in .vcs");
    				}
    				else {
    					//VCSLogger.infoLogToCmd("> Could not create pulltemp directory in .vcs");
    				}
    				
    				//#!
    				String zipFilePath = userWorkDir + File.separator + Constants.VCSFOLDER + File.separator +
    						Constants.PullTemp_FOLDER + File.separator + ".vcs.zip";
    				VCSLogger.infoLogToCmd("zipFilePath: " + zipFilePath);
    				FileOutputStream outStream = new FileOutputStream(new File(zipFilePath));
    				
    				byte buf[] = new byte[8192];
    				while(response.read(buf) > 0){
    					outStream.write(buf);
    				}
    				outStream.close();
    				response.close();
    				
    				/**
    				 * UNZIP CODE
    				 */
    				String filename = userWorkDir + File.separator + Constants.VCSFOLDER + File.separator + 
    						Constants.PullTemp_FOLDER + File.separator + ".vcs.zip";			
    				VCSLogger.infoLogToCmd("Unzipping...\nFILE:  " + filename);
    				
    				File srcFile = new File(filename);
    				
    				// create a directory with the same name to which the contents will be extracted
    				String zipPath = filename.substring(0, filename.length()-4);
    				File temp = new File(zipPath);
    				temp.mkdir();
    				
    				ZipFile zipFile = null;
    				

    				try {
    					
    					zipFile = new ZipFile(srcFile);
    					
    					// get an enumeration of the ZIP file entries
    					Enumeration<?> e = zipFile.entries();
    					
    					while (e.hasMoreElements()) {
    						
    						ZipEntry entry = (ZipEntry) e.nextElement();
    						
    						File destinationPath = new File(zipPath, entry.getName());
    						 
    						//create parent directories
    						destinationPath.getParentFile().mkdirs();
    						
    						// if the entry is a file extract it
    						if (entry.isDirectory()) {
    							continue;
    						}
    						else {
    							
    							System.out.println("Extracting file: " + destinationPath);
    							
    							BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

    							int b;
    							byte buffer[] = new byte[1024];

    							FileOutputStream fos = new FileOutputStream(destinationPath);
    							
    							BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);

    							while ((b = bis.read(buffer, 0, 1024)) != -1) {
    								bos.write(buffer, 0, b);
    							}
    							
    							bos.close();
    							bis.close();	
    						}
    					}
    					VCSLogger.infoLogToCmd("Succesful PULL operation. Check: ../.vcs/pulltemp/");
    				}
    				catch (IOException ioe) {
    					System.out.println("Error opening zip file: " + ioe);
    				}
				} //! HTTP CONN TO PULL
    			catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
    		}
    	    
    	} //~for LOOP :: line:lines<String>
		
		return null;
	}
	
	
//#!	PUSH-PULL-PUSH OP

	public VCSCommit getLocalBranchHeadwithImportAllFlag(String workingDir, String branchName) throws IOException {
		BufferedReader br = null;
		File branch = new File(workingDir + ".vcs/branches/" + branchName);
		VCSCommit localHead = null;
		String sCurrentLine;
		if(branch.exists()){
			br = new BufferedReader(new FileReader(workingDir + ".vcs/branches/" + branchName));
			while ((sCurrentLine = br.readLine()) != null) 
			{
				localHead = new VCSCommit(sCurrentLine, workingDir, VCSCommit.IMPORT_ALL);
			}
			br.close();
		}
		
		return localHead;
	}
	
	public VCSCommit getRemoteBranchHeadwithImportAllFlag(String workingDir, String branchName, String tmpDirName) throws IOException {
		BufferedReader br = null;
		File branch = new File(workingDir + ".vcs/" + tmpDirName + "/.vcs/branches/" + branchName);
		VCSCommit remoteHead = null;
		String sCurrentLine;
		if(branch.exists()){
			br = new BufferedReader(new FileReader(workingDir + ".vcs/" + tmpDirName + "/.vcs/branches/" + branchName));
			while ((sCurrentLine = br.readLine()) != null) 
			{
				remoteHead = new VCSCommit(sCurrentLine, workingDir, VCSCommit.IMPORT_ALL, tmpDirName);
			}
			br.close();
		}
		
		return remoteHead;
	}
	
	public VCSCommit getLCA(LinkedList<VCSCommit> localLL, LinkedList<VCSCommit> remoteLL) {
		
		VCSCommit LCA = null;
		
		int localLength = localLL.size();
		int remoteLength = remoteLL.size();
		int diff = 0;
		
		if(localLength>remoteLength) {
			int i = 0;
			diff = localLength-remoteLength;
			int j = i + diff;
			while(j<localLL.size() && i<remoteLL.size()) {
				//(localLL.get(j) != null) && (remoteLL.get(i) != null )
				if(localLL.get(j).equals(remoteLL.get(i))) {
					LCA = localLL.get(j);
				}
				i++;
				j++;
			}
		}
		else {
			// localListLength < remoteListLength
			int i = 0;
			diff = remoteLength-localLength;
			int j = i + diff;
			while(i<localLL.size() && j<remoteLL.size()) {
				//(localLL.get(i) != null) && (remoteLL.get(j) != null )
				if(localLL.get(i).equals(remoteLL.get(j))) {
					LCA = localLL.get(i);
					VCSLogger.debugLogToCmd("NETWORK:PUSH", "LCA FOUND: " + LCA);
				}
				i++;
				j++;
			}
			VCSLogger.infoLogToCmd("THE TWO BRANCHES DO NOT SHARE A COMMON ANCESTOR. HENCE, LCA: 'NULL'");
		}
		
		/*if(!LCA.equals(null)){
			VCSLogger.debugLogToCmd("NETWORK:PUSH", "LCA FOUND: " + LCA);
		}
		else {
			VCSLogger.infoLogToCmd("THE TWO BRANCHES DO NOT SHARE A COMMON ANCESTOR. HENCE, LCA: 'NULL'");
		}*/
		
		return LCA;
	}

	/**
	 * This is a method I write for merging two directories. 
	 * The directory of second parameter will be moved to the first one, including files and directories.
	 */
	public static void mergeTwoDirectories(File dir1, File dir2){
		String targetDirPath = dir1.getAbsolutePath();
		File[] files = dir2.listFiles();
		for (File file : files) {
			file.renameTo(new File(targetDirPath+File.separator+file.getName()));
			VCSLogger.debugLogToCmd("DIR:COPY:MERGE", file.getName() + " is moved!");
		}
	}
	
	/**
	 * push-pull-push
	 * 1. CLoned
	 * 2. Added & Commit
	 * 3. Push :
	 * 		a) pull
	 * 		b) set Lock on 'S'
	 * 		c) *merge
	 * 		d) push changes
	 * 		e) release lock on 'S'
	 * @return null
	 * @throws IOException 
	 */
	public String push(String remoteHandleName, String targetBranchName) throws IOException {
		// push origin master (push 'remoteHandleName' 'targetBranchName')
		
		/*
		 * PUSH=PULL=PUSH FORMAT
		 */
		String userWorkDir = System.getProperty("user.dir");
		//String userHomeDir = System.getProperty("user.home");
		String tmpDirName = "pulltemp";
		String workingDir = userWorkDir + File.separator;
		String confPath = userWorkDir + File.separator + Constants.VCSFOLDER + "/config";
		
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(confPath), Charset.defaultCharset());
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			VCSLogger.infoLogToCmd("Config File not found: " + e);
		}
		
		String repoUrl = null;
		for (String line : lines) {
    		if (line.contains("remote "+remoteHandleName)) {
    			int i = lines.indexOf(line);
    			i++;
    			String urlLine = lines.get(i);
    			urlLine.replaceAll("/s+", "");
    			VCSLogger.infoLogToCmd("URL LINE: " + urlLine);
    			String urlParts[] = urlLine.split("=");
    			
    			repoUrl = urlParts[1];
    		}
		}
		
		if(repoUrl.equals(null)) {
			VCSLogger.debugLogToCmd("PUSH:REPOURL:CONFIG", "FAILED TO OBTAIN repoUrl from .config");
		}
		else {
			VCSLogger.debugLogToCmd("PUSH:REPOURL:CONFIG", "REPO_URL: " + repoUrl);
		}
		
		URLConnection conn1;
		
//#!#!#!!#!# 	AUTHOR NAME ?? TEMP: "darkDefender"
		
		String author = "darkDefender";
		String LOCK_RESPONSE = "HALT";
		try {
			conn1 = new URL(repoUrl + "?REQUEST=PUSH"+"&AUTHOR="+author).openConnection();
			//conn.setRequestProperty("Accept-Charset", "UTF-8"); *Not required
			conn1.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
			conn1.setRequestProperty("Accept","*/*");
			VCSLogger.infoLogToCmd("REPO URL: " + repoUrl);
			
			//String fileNameWithExtn = repoUrl.substring( repoUrl.lastIndexOf('/')+1, repoUrl.length() );
			//String fileNameWithoutExtn = fileNameWithExtn.substring(0, fileNameWithExtn.lastIndexOf('.'));			

			InputStream response = conn1.getInputStream();
			
			FileOutputStream outStream = new FileOutputStream(new File(workingDir + "checkLockResponse"));
			
			byte buf[] = new byte[8192];
			while(response.read(buf) > 0){
				outStream.write(buf);
			}
			outStream.close();
			response.close();
			
//#!#!#! 	CHECK LOCK RESPONSE
			
			
			String LOCK_ST = null;
			String fName = workingDir + "checkLockResponse";
			List<String> filelines = Files.readAllLines(Paths.get(fName), Charset.defaultCharset());
    		
        	for (String line : filelines) {
        		String parts[] = line.split(" ");
        	    if(author.equals(parts[0])) {
        	    	LOCK_ST = parts[1];
        	    	if(LOCK_ST.equals("true")) {
        	    		LOCK_RESPONSE = "PROCEED";
        	    	}
        	    }
        	}
			
			//int status = ((HttpURLConnection) conn1).getResponseCode();
			//VCSLogger.infoLogToCmd("CONN COMPLETE -- HTTP STATUS RECEIVED: " + status);
		}
		catch(Exception e) {
			VCSLogger.debugLogToCmd("PUSH:HTTPCONN1", "ERRORCONN1: FAILED TO CONNECT TO SERVER.");
		}
		
		/*
		 * LOCK == PROCEED
		 * PULL remoteHandleName
		 * AND PROCEED...
		 */
		
//#!#!#!	PROCEEDED...
		
		if(LOCK_RESPONSE.equals("PROCEED")) {
			
			pull(remoteHandleName);
			
			VCSCommit localObject = getLocalBranchHeadwithImportAllFlag(workingDir, targetBranchName);
			VCSCommit remoteObject = getRemoteBranchHeadwithImportAllFlag(workingDir, targetBranchName, tmpDirName);
			
			/*
			 * Finding LCA for the two linked lists,
			 * namely localObject and remoteObject
			 * 
			 * 1. Constructing a Linked List of type VCSCommit corresponding to each local and remote
			 * 2. Add respective parents of each VCSCommit object in the linked list
			 * 3. In case a VCSCommit object has more than one parent, select the first parent i.e. 0th positioned parent
			 * 4. 
			 * 
			 */
			
			LinkedList<VCSCommit> localLL = new LinkedList<VCSCommit>();
			LinkedList<VCSCommit> remoteLL = new LinkedList<VCSCommit>();
			
			VCSCommit localparentObj = localObject;
			localLL.add(localparentObj);
			while(!localparentObj.getParentCommits().isEmpty()) {
				localparentObj=localparentObj.getParentCommits().get(0);
				localLL.add(localparentObj);
			}
			
			VCSCommit remoteparentObj = remoteObject;
			remoteLL.add(remoteparentObj);
			while(!remoteparentObj.getParentCommits().isEmpty()) {
				remoteparentObj=remoteparentObj.getParentCommits().get(0);
				remoteLL.add(remoteparentObj);
			}
			
			VCSCommit LCA = getLCA(localLL, remoteLL);
			
			
			/*
			 * FOR EVERY NEW ELEMENT (VCSCommit Object) FROM REMOTE DO:
			 * 0. FIND SUCH ELEMENTS (STACK|QUEUE)
			 * 1. WRITE ORIGINALS TO DISK
			 * 2. WRITE COMMIT TO DISK
			 */
			
			Queue<VCSCommit> newElems = new LinkedList<VCSCommit>();
			Stack<VCSCommit> writeElems = new Stack<VCSCommit>();
			
			newElems.add(remoteObject);
			
			while(!newElems.isEmpty()) {
				VCSCommit item = newElems.remove();
				writeElems.add(item);
				
				ArrayList<VCSCommit> itemsList = item.getParentCommits(); 
				for(VCSCommit e : itemsList) {
					if(!e.equals(LCA)){
						newElems.add(e);
					}
				}
			}
			
			VCSLogger.infoLogToCmd("Moving further... WRITING FINAL ELEMS...");
			
			ArrayList<VCSCommit> finalElements = new ArrayList<VCSCommit>();
			while(!writeElems.empty()) {
				finalElements.add(writeElems.pop());
			}
			
			/*
			 * MERGE TWO DIRECTORIES' OBJECTS (objectfinalElements are merged)
			 */
			String sourceDir1Path = workingDir + ".vcs/objects/";
			String sourceDir2Path = workingDir + ".vcs/pulltemp/.vcs/objects/";
	 
			File dir1 = new File(sourceDir1Path);
			File dir2 = new File(sourceDir2Path);
	 
			mergeTwoDirectories(dir1, dir2);

			
			for(VCSCommit de : finalElements){
				
				/*String sourceDir1Path = workingDir + ".vcs/objects/";
				String sourceDir2Path = "/home/programcreek/Desktop/d2";
		 
				File dir1 = new File(sourceDir1Path);
				File dir2 = new File(sourceDir2Path);
		 
				mergeTwoDirectories(dir1, dir2);*/
				
				/*if(de.getTree().writeOriginalToDisk()) {
					VCSLogger.debugLogToCmd("NETWORK:PUSH:WRITEORIGINALSTODISKS", "NEW ELEMENTS FROM REMOTE WRITTEN SUCCESSFULLY TO DISK.");
				}
				else {
					VCSLogger.debugLogToCmd("NETWORK:PUSH:WRITEORIGINALSTODISKS", "#! NEW ELEMENTS FROM REMOTE WRITE FAILED TO DISK.");
				}
				if(de.writeCommitToDisk()) {
					VCSLogger.debugLogToCmd("NETWORK:PUSH:WRITECOMMITSTODISKS", "NEW ELEMENTS FROM REMOTE WRITTEN SUCCESSFULLY TO DISK.");
				}
				else {
					VCSLogger.debugLogToCmd("NETWORK:PUSH:WRITECOMMITSTODISKS", "#! NEW ELEMENTS FROM REMOTE WRITE FAILURE TO DISK.");
				}*/
				VCSLogger.debugLogToCmd("PUSH:OBJECTWRITES","WRITE these to DISK: " + de.getObjectHash());
			}
			
			/*
			 * MERGE(localObject, remoteObject, LCA) if LCA is non NULL
			 */

			mergeBranch(workingDir, localObject.getObjectHash(), remoteObject.getObjectHash(), LCA.getObjectHash());
			VCSLogger.infoLogToCmd("MERGE SUCCESSFUL... Connecting AGAIN-");
			
			/*
			 *  1st push
			 *  if conflict wait (merge)
			 *  commit (Resolve Conflicts)
			 *  push 2nd time push
			 */
			
			/*
			 * UPDATE INFORMATION OBTAINED AFTER MERGE
			 * 1. DEVELOPER LIST
			 * 2. CURRENT BRANCH HEAD
			 * 3. HEAD
			 */
			
			
			/*
			 * Compress the current direcctory & PUSH FINAL CHANGES TO SERVER.
			 * Here, Apache HttpClient library is used.
			 */
			
			
			if(!userWorkDir.equals(null)) {
	    		
	    		File directoryToZip = new File(userWorkDir);
	    		List<File> fileList = new ArrayList<File>();

	    		ZipDirectory.getAllFiles(directoryToZip, fileList);
	    		ZipDirectory.writeZipFile(directoryToZip, fileList); //Writes to the working directory
	    		VCSLogger.infoLogToCmd("---Done");
	    	}
	    	else {
	    		VCSLogger.infoLogToCmd("The repositiory is Empty or not instantiated on this local machine.");
	    	}
	    	
	    	String party[] = userWorkDir.split("/");
	    	String fname = userWorkDir + "/" + party[(party.length)-1] + ".zip";
	    	VCSLogger.debugLogToCmd("PUSHZIP", "ZIP FILE TO BE PUSHED: " + fname);
	    	
	    	
			FileUpload fUpload = new FileUpload();
			
			File file = new File (fname) ;
			//repoUrl required format: 127.0.0.1:8009
	        String response = fUpload.executeMultiPartRequest("http://127.0.0.1:8009", file, file.getName(), "File Upload proceeding...") ;
	        System.out.println("Response received : " + response);
			
			
		}//~LOCK_RESPONSE == PROCEED
		else {
			VCSLogger.infoLogToCmd("A LOCK EXISTS ON THE REMOTE LOCATION. PLEASE WAIT...");
		}
		
		
		return null;
	}


	public void vcsStatus(String workDir) {
		try {
			StringBuilder untrackedFiles = new StringBuilder();
			StringBuilder modifiedFiles = new StringBuilder();
			VCSCommit headCommit = getHead(workDir);
			ArrayList<String> files = new ArrayList<String>();
			listAllFiles(workDir, files, workDir);
			VCSTree headTree = headCommit.getTree();
			VCSBlob tempBlob = null;
			for(String filename : files){
				AbstractVCSTree fileTree = headTree.findTreeIfExist(filename, 0);
				if(fileTree == null){
					untrackedFiles.append("\t");
					untrackedFiles.append(filename);
					untrackedFiles.append("\n");
				}else{
					tempBlob = new VCSBlob("Temp", workDir + filename, workDir);
					if(tempBlob != null){
						if(!fileTree.getObjectHash().equals(tempBlob.getObjectHash())){
							modifiedFiles.append("\t");
							modifiedFiles.append(filename);
							modifiedFiles.append("\n");
						}
					}
				}
			}
			StringBuilder result = new StringBuilder();
			result.append("\nModified Files:\n");
			result.append(modifiedFiles);
			result.append("Untracked Files:\n");
			result.append(untrackedFiles);
			VCSLogger.infoLogToCmd(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
