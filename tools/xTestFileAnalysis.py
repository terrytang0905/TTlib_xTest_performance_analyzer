#!/usr/bin/python
# Filename: xTestFileAnalysis.py

import sys

class xTestAnalysis:
  targetfilename='xTestAnalysis.txt'

  def __init__(self,filename,*comparatorfilename):
    self.filename=filename
    if len(comparatorfilename) == 1:
      self.comparatorfilename=comparatorfilename[0] 
    
  def FileAnalysis(self):
    sourcefile=file(self.filename)
    targetfile=file(self.targetfilename,'w')
    
    while True:
      line=sourcefile.readline()
      arraylist=line.split(',') 
      if len(line)==0:
        break
      if len(arraylist) > 14:
        targetfile.write(line)
        
    sourcefile.close()
    targetfile.close()
    
  def FileAnalysisComparator(self):
    try:    
        sourcefile=file(self.filename)
        comparatorfile=file(self.comparatorfilename)
        targetfile=file(self.targetfilename,'w')
        tempStr=''
        key=0
        
        while True:
          line=sourcefile.readline()
          comparatorline=comparatorfile.readline()
          arraylist=line.split(',') 
          comparatorarraylist=comparatorline.split(',')
          if len(line)==0 and len(comparatorline)==0:
            break
          elif len(line)==0 and len(comparatorline) > 0 or len(comparatorline)==0 and len(line) > 0 :
            Exception.message('Couldnt be compared because of length difference.')   
          if len(arraylist) > 14 and len(comparatorarraylist) > 14:
            key = 1
            targetfile.write(line)
            tempStr+=comparatorline
          elif len(arraylist) == 14 and len(comparatorarraylist) == 14 and key == 1:
            targetfile.write(tempStr+'\n') 
            tempStr = ''
            key = 0
            
    finally:
        sourcefile.close()
        comparatorfile.close()
        targetfile.close()
          
test=xTestAnalysis('C:\\Resource\\Benchmark\\ParallelQueryPerf\\Round8_seperate\\xpl_pq_5_1359455013494.result.txt','C:\\Resource\\Benchmark\\ParallelQueryPerf\\Round8_seperate\\xpl_pq_6_1359528832608.result.txt')
test.FileAnalysisComparator()
