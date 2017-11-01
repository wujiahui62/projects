setwd("/Users/jiahuiwu/Desktop/CIS660/project")
options(max.print = 100000)
FD_data <- read.csv("sample_data_logis.csv", header = TRUE)
#eliminate the first row
FD_data <- FD_data[, -1]
#there are 7 attributes in the FD_data set, the first one "isFraud" is class variable
head(FD_data)
names(FD_data)
#split training set and testing set
set.seed(0)
train = sample(1:nrow(FD_data), nrow(FD_data) * 0.7)
test = -train
FD_train = FD_data[train,]
FD_test = FD_data[test,]
train_target <- FD_train[, 1]
testing_target <- FD_test[, 1]
dim(FD_train)
dim(FD_test)
require(class)
require(rminer)
?knn
# take k as the sqrt of the total obs
sqrt(16426)
# a prediction for the classification of all of values in testing
knn_m1 <- knn(train = FD_train[, -1], test = FD_test[, -1], 
          cl = train_target, k = 128)
library(rminer)
mmetric(as.factor(testing_target), as.factor(knn_m1), 
        metric = c("CONF", "ACC", "CE", "TPR", "PRECISION"))
#CONF: confusion table
#ACC: classification accurary rate
#CE: classification error
#TPR: true positive rate, sensitivity or recall
#TPR2 stands for recall, TP/(TP + FN)
#PRECISION TP/(TP + FP)

#In this case, we want to reduce the value of False-negative, 
#so I tried different k value
#k=20
knn_m2 <- knn(train = FD_train[, -1], test = FD_test[, -1], 
              cl = train_target, k = 20)
mmetric(as.factor(testing_target), as.factor(knn_m2), 
        metric = c("CONF", "ACC", "CE", "TPR", "PRECISION"))

#k=10
knn_m3 <- knn(train = FD_train[, -1], test = FD_test[, -1], 
              cl = train_target, k = 10)
mmetric(as.factor(testing_target), as.factor(knn_m3), 
        metric = c("CONF", "ACC", "CE", "TPR", "PRECISION"))

#k=5
knn_m4 <- knn(train = FD_train[, -1], test = FD_test[, -1], 
              cl = train_target, k = 7)
mmetric(as.factor(testing_target), as.factor(knn_m4), 
        metric = c("CONF", "ACC", "CE", "TPR", "PRECISION"))

#To find the best K value, I use a loop to loop through 5 to 20
knn_result <- list()
knn_total <- array()
typeof(knn_m_opt)
for(i in 5:20){
  knn_m_opt <- knn(train = FD_train[, -1], test = FD_test[, -1], 
                cl = train_target, k = i)
  knn_result[[i]] <-mmetric(as.factor(testing_target), as.factor(knn_m_opt), 
          metric = "ALL")
  print(i)
  print(mmetric(as.factor(testing_target), as.factor(knn_m_opt), 
                metric = "CONF"))
  knn_total <- cbind(knn_total, knn_result[[i]])
}
knn_total <- knn_total[,-1]
knn_total_recall <- knn_total[9,]
max(knn_total_recall)

#by comparing the value of recall, k = 7 gives the best recall value

#research has shown that 10-fold CV repeated 10 times is the best place to start,
#so we'll start from there and see how it goes

#cross validation
#after doing CV, I throw away all the resampled models and start over
#CV is only used to estimate the out-of-sample error for my model
#The train function in caret does a different kind of resampling known as bootstrap 
#validation
#10-fold CV for KNN
set.seed(1234)
#create the folds
require(caret)
folds <- createFolds(FD_data$isFraud, k = 10)
cv_knn_result <- list()
cv_knn_conf <- list()
cv_knn_total <- array()
#loop 10 times 
for(i in 1:10){
  CV_train <- FD_data[(-folds[[i]]),]
  CV_test <- FD_data[folds[[i]],]
  CV_model <- knn(train = CV_train, test = CV_test, 
                  cl = CV_train[,1], k = 7)
  cv_knn_result[[i]] <- mmetric(as.factor(CV_test[,1]), 
                                as.factor(CV_model), metric = "ALL")
  cv_knn_total <- cbind(cv_knn_total, cv_knn_result[[i]])
  cv_knn_conf[[i]] <- mmetric(as.factor(CV_test[,1]), 
                                as.factor(CV_model), metric = "CONF")
}
rowMeans(cv_knn_total[, -1])

#an alternative way to for loop
#instead of using for loop, I can also apply function to each fold
?lapply
cv_knn_result2 <- lapply(folds, function(x){
  CV_train <- FD_data[-x,]
  CV_test <- FD_data[x,]
  CV_model <- knn(train = CV_train, test = CV_test, 
                  cl = CV_train[,1], k = 10)
  return(mmetric(as.factor(CV_test[,1]), 
                 as.factor(CV_model), metric = "ALL"))
})
rowMeans(cbind(cv_knn_result2$Fold01, cv_knn_result2$Fold02, cv_knn_result2$Fold03,
               cv_knn_result2$Fold04, cv_knn_result2$Fold05, cv_knn_result2$Fold06,
               cv_knn_result2$Fold07, cv_knn_result2$Fold08, cv_knn_result2$Fold09,
               cv_knn_result2$Fold10))

#######################################

#clustering the data using the two transaction type without sample
#the sample size is 2770409
raw_data <- read.csv("cashout_transfer_data.csv", header = TRUE)
head(raw_data)
str(raw_data)
names(raw_data)
#copy the data to a new set of data, normalize the new set
new_data <- cbind(raw_data)
#replace type with numbers, transfer as 0, cashout as 1
new_data$type <- as.character(new_data$type)
new_data$type[which(new_data$type == "TRANSFER")] <- "0"
new_data$type[which(new_data$type == "CASH_OUT")] <- "1"
new_data$type <- as.integer(new_data$type)

#normalize the value of feature step
x <- new_data$step
a = min(x)
b = max(x)
norm <- function(x){(x - a) / (b - a)}
new_data$step <- apply(new_data[1], 2, norm)

#calculate signed log basen10
signedlog10 = function(x) {
  ifelse(abs(x) <= 1, 0, sign(x)*log10(abs(x)))
}

new_data$amount <- lapply(new_data$amount, signedlog10)
new_data$amount <- as.double(new_data$amount)
new_data$oldbalanceOrg <- lapply(new_data$oldbalanceOrg, signedlog10)
new_data$oldbalanceOrg <- as.double(new_data$oldbalanceOrg)
new_data$newbalanceOrig <- lapply(new_data$newbalanceOrig, signedlog10)
new_data$newbalanceOrig <- as.double(new_data$newbalanceOrig)
new_data$oldbalanceDest <- lapply(new_data$oldbalanceDest, signedlog10)
new_data$oldbalanceDest <- as.double(new_data$oldbalanceDest)
new_data$newbalanceDest <- lapply(new_data$newbalanceDest, signedlog10)
new_data$newbalanceDest <- as.double(new_data$newbalanceDest)

summary(new_data)
head(new_data)
cluster_result <- kmeans(new_data, 2)
cluster_result$size
table(new_data$isFraud, cluster_result$cluster)
?kmeans

cluster_result <- kmeans(new_data, 10)
cluster_result$size
table(new_data$isFraud, cluster_result$cluster)

hist(as.numeric(new_data$amount))
hist(as.numeric(new_data$step))
hist(as.numeric(new_data$type))
hist(as.numeric(new_data$oldbalanceOrg))
hist(as.numeric(new_data$newbalanceOrig))
hist(as.numeric(new_data$oldbalanceDest))
hist(as.numeric(new_data$newbalanceDest))
hist(as.numeric(new_data$isFraud))
hist(as.numeric(new_data$isFlaggedFraud))

cluster_result <- kmeans(FD_data, 2)
cluster_result$size
table(FD_data$isFraud, cluster_result$cluster)

#feature selection

#data preprocessing, sample data from normal dataset in cash-out and transfer category
#to balance the skewed data. The fraud data is 8213, so I sample 8213 normal data from
#the normal dataset (276216 records) to make a ratio of 1:1

#load fraud data and normal data
fraud_data <- read.csv("fraud_data.csv", header = TRUE)
normal_data <- read.csv("normal_data.csv", header = TRUE)
dim(fraud_data)
dim(normal_data)
names(fraud_data)
#sample 8213 rows from normal data
set.seed(0)
normal_data_sample <- normal_data[sample(nrow(normal_data), 8213), ]
summary(normal_data_sample)

#combine fraud data and sampled normal data sample
test_data <- rbind(fraud_data, normal_data_sample)
summary(test_data)

#shuffle the test data 
set.seed(9850)
gp <- runif(nrow(test_data))
#mix the rows
test_data <- test_data[order(gp),]

#use min-max to normalize feature step to 0-1
x <- test_data$step
a = min(x)
b = max(x)
norm <- function(x){(x - a) / (b - a)}
test_data$step <- apply(test_data[1], 2, norm)
summary(test_data)

#replace transfer and cash_out in feature 'type' with number 0 and 1
test_data$type <- as.character(test_data$type)
test_data$type[which(test_data$type == "TRANSFER")] <- "0"
test_data$type[which(test_data$type == "CASH_OUT")] <- "1"
test_data$type <- as.integer(test_data$type)

#use log10 to normalize the numeric data
signedlog10 = function(x) {
  ifelse(abs(x) <= 1, 0, sign(x)*log10(abs(x)))
}

test_data$amount <- lapply(test_data$amount, signedlog10)
test_data$amount <- as.double(test_data$amount)
test_data$oldbalanceOrig <- lapply(test_data$oldbalanceOrig, signedlog10)
test_data$oldbalanceOrig <- as.double(test_data$oldbalanceOrig)
test_data$newbalanceOrig <- lapply(test_data$newbalanceOrig, signedlog10)
test_data$newbalanceOrig <- as.double(test_data$newbalanceOrig)
test_data$oldbalanceDest <- lapply(test_data$oldbalanceDest, signedlog10)
test_data$oldbalanceDest <- as.double(test_data$oldbalanceDest)
test_data$newbalanceDest <- lapply(test_data$newbalanceDest, signedlog10)
test_data$newbalanceDest <- as.double(test_data$newbalanceDest)

#all the data is transformed into numeric values and normalized
summary(test_data)
dim(test_data)
#feature selection
#it extracts the importance of each variable and it will leverage the variable importance
#function of our model. For example, tree models will total the importance of individual trees
#it also return MSC(the variable of the estimator) and the variance of the estimator and
#RMSE the standard deviation of the sample and it will scale them too so we have a vote from 
#all the models together
library(fscaret)
#to find out what models the fscaret package supports
funcRegPred
#split the data into training and testing set
set.seed(0)
splitIndex <- createDataPartition(test_data$isFraud, p = 0.7, list = FALSE, times = 1)
trainDF <- test_data[splitIndex,]
testDF <- test_data[-splitIndex,]
head(trainDF)
#I will be using the following models
fs_models <- c("knn", "rpart", "bayesglm", "svmPoly")
fs <- fscaret(trainDF, testDF, preprocessData = TRUE,
              Used.funcRegPred = fs_models, with.labels = TRUE,
              supress.output = FALSE, no.cores = 2)
names(fs)
#the RMSE and MSE of all models and the scaled sum of what it thinks the variable importance are 
fs$VarImp
#give the lable
fs$PPlabels
#we can tell that 4, 6, 3, 5, 7, 2, 1 are in order of importance
#they are oldbalanceOrig, oldbalanceDest, amount, newbalanceOrig, newbalanceDest, type, step
#we can compare the result of knn and svmPoly and see the MSE given by different features are
#quite different, so these two may work really work together in an ensemble to predict the data.
#if two models give very similar result, using two in an emsemble is a bit of a waste

#using the results of feature selection to compare which features should be selected
#first I need to reorder the column of the normalized dataset in a specific order
#which is 4, 6, 3, 5, 7, 2, 1, the class feature "isFraud" is put in the first column
test_data_reorder <- test_data[, c(8, 4, 6, 3, 5, 7, 2, 1)]
head(test_data_reorder)
names(test_data_reorder)
dim(test_data_reorder)

knn_model <- knn(train = train_reorder[, -1], test = test_reorder[, -1], 
                 cl = train_reorder[, 1], k = 7)
table(test_reorder[,1], knn_model)
mmetric(as.factor(test_reorder[, 1]), as.factor(knn_model), 
              metric = "CONF")


#loop through the features
#start a for loop to loop the attribute
maxRecall_knn = 0
bestFeature_knn = 8
for(i in 8: 3){
  #each time assign column 1-i to data
  data <- test_data_reorder[, c(1:i)]
  #split the data into training and testing
  set.seed(0)
  splitIndex <- createDataPartition(data$isFraud, p = 0.7, list = FALSE, times = 1)
  train_reorder <- data[splitIndex,]
  test_reorder <- data[-splitIndex,]
  #knn
  library(class)
  knn_model <- knn(train = train_reorder[, -1], test = test_reorder[, -1], 
                   cl = train_reorder[, 1], k = 7)
  table <- table(test_reorder[, 1], knn_model)
  a = as.vector(table[1])
  b = as.vector(table[2])
  c = as.vector(table[3])
  d = as.vector(table[4])
  currRecall <- d / (b + d)
  if(currRecall > maxRecall_knn){
    maxRecall_knn = currRecall
    bestFeature_knn = i
  }
}

#using C50
maxRecall_C50 = 0
bestFeature_C50 = 8
for(i in 8: 3){
  #each time assign column 1-i to data
  data <- test_data_reorder[, c(1:i)]
  #split the data into training and testing
  set.seed(0)
  splitIndex <- createDataPartition(data$isFraud, p = 0.7, list = FALSE, times = 1)
  train_reorder <- data[splitIndex,]
  test_reorder <- data[-splitIndex,]
  #knn
  library(C50)
  model = C5.0(train_reorder[, -1], as.factor(train_reorder[, 1]))
  pred = predict(model, test_reorder)
  #confusion table
  table = table(test_reorder[, 1], Predicted = pred)
  a = as.vector(table[1])
  b = as.vector(table[2])
  c = as.vector(table[3])
  d = as.vector(table[4])
  currRecall <- d / (b + d)
  if(currRecall > maxRecall_C50){
    maxRecall_C50 = currRecall
    bestFeature_C50 = i
  }
}

#using rpart
maxRecall_rpart = 0
bestFeature_rpart = 8
for(i in 8: 3){
  #each time assign column 1-i to data
  data <- test_data_reorder[, c(1:i)]
  #split the data into training and testing
  set.seed(0)
  splitIndex <- createDataPartition(data$isFraud, p = 0.7, list = FALSE, times = 1)
  train_reorder <- data[splitIndex,]
  test_reorder <- data[-splitIndex,]
  #knn
  library(rpart)
  model = rpart(isFraud ~., train_reorder, method = "class",
                control = rpart.control(cp = 0))
  pred = predict(model, test_reorder, type="class")
  #confusion table
  table = table(test_reorder[, 1], Predicted = pred)
  a = as.vector(table[1])
  b = as.vector(table[2])
  c = as.vector(table[3])
  d = as.vector(table[4])
  currRecall <- d / (b + d)
  if(currRecall > maxRecall_rpart){
    maxRecall_rpart = currRecall
    bestFeature_rpart = i
  }
}

maxRecall_knn
bestFeature_knn
maxRecall_C50
bestFeature_C50
maxRecall_rpart
bestFeature_rpart
