#since the data is organized in two group, fraud and normal, I start 
#to shuffle the data
#first I generate nrows of data from 0 - 1
set.seed(9850)
gp <- runif(nrow(data2))
#mix the rows
data2 <- data2[order(gp),]
#summary(data2)

#The packages are usually downloaded to one of the folders in .libPaths() 
#You can use sapply(.libPaths(), list.files) to see what's in each 
#(if there is more than one folder) –
sapply(.libPaths(), list.files)

#remove the old package and install again because sometimes error happen if
#you try to install a newer version on top of the old version
inst_packages <-  installed.packages()
if ("RWekajars" %in% inst_packages[, 1]) { 
  #uninstalls package
  remove.packages("RWekajars")
  #re-installs package
  install.packages("RWekajars") 
} 
