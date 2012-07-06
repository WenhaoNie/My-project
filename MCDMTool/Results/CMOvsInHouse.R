rm(list=ls())
result1=read.csv("/Users/wenhaonie/Dropbox/Eclipse Projects/MCDMTool/CMOvsInHouseS_1", header=FALSE)
attach(result1)
result1=result1[V1<1,]
num1=dim(result1)[1]
CMOcontent1=result1[,5]+result1[,6]+result1[,7]+result1[,8]+result1[,9]
scenario1CMOweights=sum(CMOcontent1)/num1
scenario1CMOweights

rm(list=ls())
result2=read.csv("/Users/wenhaonie/Dropbox/Eclipse Projects/MCDMTool/CMOvsInHouseS_2",header=FALSE)
attach(result2)
result2=result2[V1<1,]
num2=dim(result2)[1]
CMOcontent2=result2[,5]+result2[,6]+result2[,7]+result2[,8]+result2[,9]
scenario2CMOweights=sum(CMOcontent2)/num2
scenario2CMOweights

rm(list=ls())
result3=read.csv("/Users/wenhaonie/Dropbox/Eclipse Projects/MCDMTool/CMOvsInHouseS_3",header=FALSE)
attach(result3)
result3=result3[V1<1,]
num3=dim(result3)[1]
CMOcontent3=result3[,5]+result3[,6]+result3[,7]+result3[,8]+result3[,9]
scenario3CMOweights=sum(CMOcontent3)/num3
scenario3CMOweights

