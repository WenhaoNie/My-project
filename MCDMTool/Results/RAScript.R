rm(list=ls())

setwd("/Users/wenhaonie/Dropbox/Eclipse Projects/MCDMTool/Results")

result=read.csv("RA", header=FALSE)

result[,3:length(result)]=result[,3:length(result)]/1000

result=t(result)

postscript("RAplot_2", width=1200,height=1000)

par(mfrow=c(4,5), mar=c(5,5,5,3))

for(i in 1:20){
	plot(result[3:1003, i], xlab="No.of Monte Carlo Trials", ylab="Running Average of NPVs($million)")
	title(i)	
}

dev.off()


