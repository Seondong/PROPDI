install.packages("PerformanceAnalytics")
install.packages("sqldf")
install.packages("plotrix")
install.packages("sfsmisc")
install.packages(c("ggplot2","RColorBrewer","scales"))
library(ggplot2); library(scales); library(grid); library(RColorBrewer)
library(PerformanceAnalytics)
library(plotrix)
library(sqldf)
library(sfsmisc)
Sys.getlocale()


################################ Sample Plot #########################################

# Basic plot test
ggplot(inputData, aes(x,y)) +
  geom_point()


fte_theme <- function() {
  
  # Generate the colors for the chart procedurally with RColorBrewer
  palette <- brewer.pal("Greys", n=9)
  color.background = palette[1]
  color.grid.major = palette[3]
  color.axis.text = palette[6]
  color.axis.title = palette[7]
  color.title = palette[9]
  
  # Begin construction of chart
  theme_bw(base_size=9) +
    
    # Set the entire chart region to a light gray color
    theme(panel.background=element_rect(fill=color.background, color=color.background)) +
    theme(plot.background=element_rect(fill=color.background, color=color.background)) +
    theme(panel.border=element_rect(color=color.background)) +
    
    # Format the grid
    theme(panel.grid.major=element_line(color=color.grid.major,size=.25)) +
    theme(panel.grid.minor=element_blank()) +
    theme(axis.ticks=element_blank()) +
    
    # Format the legend, but hide by default
    theme(legend.position="none") +
    theme(legend.background = element_rect(fill=color.background)) +
    theme(legend.text = element_text(size=7,color=color.axis.title)) +
    
    # Set title and axis labels, and format these and tick marks
    theme(plot.title=element_text(color=color.title, size=10, vjust=1.25)) +
    theme(axis.text.x=element_text(size=7,color=color.axis.text)) +
    theme(axis.text.y=element_text(size=7,color=color.axis.text)) +
    theme(axis.title.x=element_text(size=8,color=color.axis.title, vjust=0)) +
    theme(axis.title.y=element_text(size=8,color=color.axis.title, vjust=1.25)) +
    
    # Plot margins
    theme(plot.margin = unit(c(0.35, 0.2, 0.3, 0.35), "cm"))
}


#####################################################################################################################
### Property별 Distinct한 instance (subject) - propko2014만 

inputPath <- file.path("C:", "users", "user", "git", "PROPDI", "intermediateResult_ko_2014", "sortedproptypemaxfreq.csv")

inputData = read.csv(inputPath, header=FALSE, sep=",")

col_headings <- c("property", "numIns")
names(inputData) <- col_headings

rank = dim(inputData)[1]


# Generate the data
x <- 1:rank
y <- inputData$numIns


# Basic plot test - with the above theme
ggplot(inputData, aes(x,y)) +
  geom_jitter() +
  fte_theme() +
  labs(title="Number of instances for property", x="Property Rank", y="# of Distinct Instances") +
  scale_x_continuous(breaks=seq(0,rank, by=1000), labels=comma) +
  scale_y_log10(breaks=c(1, 10, 100, 1000, 10000, 100000), labels=comma) + 
  geom_hline(yintercept=0.1, size=0.4, color="black")

ggsave("D:/Dropbox/Document/ISWC2016_PROPDI/Figures/tutorial_1.png", dpi=300, width=4, height=3)




#####################################################################################################################
### PROPKO(2014) PROPDI RESULT 

inputPath <- file.path("C:", "users", "user", "git", "PROPDI", "intermediateResult_ko_2014", "sortedproptypemaxfreq.csv")
inputPath2 <- file.path("C:", "users", "user", "git", "PROPDI", "finalResult_ko_2014", "PROPDI_top1_result.csv")

inputData = read.csv(inputPath, header=FALSE, sep=",")
inputData2 = read.csv(inputPath2, header=FALSE, sep=",")

col_headings1 <- c("property", "numIns")
names(inputData) <- col_headings1

col_headings2 <- c("property", "type")
names(inputData2) <- col_headings2

df <- sqldf("SELECT property, numIns, type FROM inputData LEFT JOIN inputData2 USING(property)")

write.csv(df, file = "D:/Dropbox/Document/ISWC2016_PROPDI/Data/PROPDI_propko2014_domain_result.csv")



#####################################################################################################################
# Property별 Distinct한 instance (subject) - propko 2014-2015 변화 
## http://stackoverflow.com/questions/2564258/plot-two-graphs-in-same-plot-in-r
### 추가 필요사항: DBO에 대한 정보를 일부 추가해서 넣을 필요 있음. (DBO에 비해 localized property가 커버리지가 훨씬 크다는 걸 강조)

inputPath1 <- file.path("C:", "users", "user", "git", "PROPDI", "intermediateResult_ko_2014", "sortedproptypemaxfreq.csv")
inputPath2 <- file.path("C:", "users", "user", "git", "PROPDI", "intermediateResult_ko_2015", "sortedproptypemaxfreq.csv")
inputPath3 <- file.path("C:", "users", "user", "git", "PROPDI", "intermediateResult_de_2015", "sortedproptypemaxfreq.csv")
inputPath4 <- file.path("C:", "users", "user", "git", "PROPDI", "intermediateResult_es_2015", "sortedproptypemaxfreq.csv")


inputData1 = read.csv(inputPath1, header=FALSE, sep=",")
inputData2 = read.csv(inputPath2, header=FALSE, sep=",")
inputData3 = read.csv(inputPath3, header=FALSE, sep=",")
inputData4 = read.csv(inputPath4, header=FALSE, sep=",")

col_headings1 <- c("property", "numIns")
names(inputData1) <- col_headings1
col_headings2 <- c("property", "numIns")
names(inputData2) <- col_headings2
col_headings3 <- c("property", "numIns")
names(inputData3) <- col_headings3
col_headings4 <- c("property", "numIns")
names(inputData4) <- col_headings4

rank1 = dim(inputData1)[1]
rank2 = dim(inputData2)[1]
rank3 = dim(inputData3)[1]
rank4 = dim(inputData4)[1]

x1 <- 1:rank1
y1 <- inputData1$numIns
x2 <- 1:rank2
y2 <- inputData2$numIns
x3 <- 1:rank3
y3 <- inputData3$numIns
x4 <- 1:rank4
y4 <- inputData4$numIns



# Basic plot test - with the above theme
ggplot(inputData4, aes(x4, y4)) +
  geom_jitter() +
  fte_theme() +
  labs(title="Number of instances for property", x4="Property Rank", y4="# of Distinct Instances") +
  scale_x_continuous(breaks=seq(0,rank4, by=1000), labels=comma) +
  scale_y_log10(breaks=c(1, 10, 100, 1000, 10000, 100000, 1000000), labels=comma) + 
  geom_hline(yintercept=0.1, size=0.4, color="black")

# +
#   geom_line(aes(x=x2, y=y2, colour = "var0")) +
#   geom_line(aes(x=x3, y=y3, colour = "var0")) +
#   geom_line(aes(x=x4, y=y4, colour = "var0"))  

ggsave("D:/Dropbox/Document/ISWC2016_PROPDI/Figures/tutorial_2.png", dpi=300, width=4, height=3)







plot(1:rank4, inputData4$numIns, log="xy", main="Number of instances for property", 
     xlab="Rank of property")

lines(1:rank1, inputData1$numIns, col="red")
lines(1:rank2, inputData2$numIns, col="green")
lines(1:rank3, inputData3$numIns, col="blue")

legend(1500, 500000, c("Ko-2014", "Ko-2015", "De-2015", "Es-2015"), col = c("red", "green", "blue", "black"),
       lty = c(1, 1, 1, 1), pch = c(1, 1, 1, 1), merge = TRUE)




# Class에 속하는 Property별 값 최대값 - propko 2014 (classMaxConf_propko_2014.pdf)

par(mar = c(5, 5, 5, 2))
inputPath <- file.path("C:", "users", "user", "git", "PROPDI", "intermediateResult_ko_2014", "classMaxConf.csv")

inputData = read.csv(inputPath, header=FALSE, sep=",")
newinputdata <- inputData[order(-inputData$V2),]

rank = dim(newinputdata)[1]
ggplot(1:rank, newinputdata$V2, main="Maximum value of property for each class", 
     xlab="Rank of class", ylab="Max RC_{ij}")

print(newinputdata)


# Class별 인스턴스 개수 

inputPath <- file.path("C:", "users", "user", "git", "PROPDI", "intermediateResult_de_2015", "insttypefreq.csv")
inputData = read.csv(inputPath, header=FALSE, sep=",")
newinputdata <- inputData[order(-inputData$V2),]
col_headings <- c("class", "numIns")
names(newinputdata) <- col_headings


rank = dim(newinputdata)[1]
plot(1:rank, newinputdata$numIns, main="Number of instances for class", 
     xlab="Rank of class")


names = newinputdata$class
par(mar = c(10, 18, 4, 1))
barplot(newinputdata$numIns, log="x", xlab="Rank of class",horiz=TRUE, names.arg=names, cex.names=0.7, las=1)
l="Number of instances for each class"
mtext(l,side = 3, at = getFigCtr()[1], line = 0.8, cex=1.2) 



# Property별 클래스 frequency의 최대값 (property를 선택할 수 있게) 

inputPath <- file.path("C:", "users", "user", "git", "PROPDI", "intermediateResult_ko_2014", "proptypefreq.csv")
inputData = read.csv(inputPath, header=FALSE, sep=",")
propName = "http://ko.dbpedia.org/property/출판사"

newinputdata <- inputData[grep(propName, inputData$V1, ignore.case=T),]  

newinputdata <- newinputdata[order(-newinputdata$V3),]

rank = dim(newinputdata)[1]
names = newinputdata$V2
barplot(newinputdata$V3, xlab="Rank of class", horiz=TRUE, names.arg=names, las=1, main=paste("Relatedness score of property\n", propName, sep=" "), )




# Property별 클래스 value의 최대값 (property를 선택할 수 있게) 

inputPath <- file.path("C:", "users", "user", "git", "PROPDI", "intermediateResult_ko_2014", "proptypeconf.csv")
inputData = read.csv(inputPath, header=FALSE, sep=",")
propName = "http://ko.dbpedia.org/property/유역면적반올림"

newinputdata <- inputData[grep(propName, inputData$V1, ignore.case=T),]  

newinputdata <- newinputdata[order(-newinputdata$V3),]

rank = dim(newinputdata)[1]
names = newinputdata$V2
barplot(newinputdata$V3, xlab="Rank of class", horiz=TRUE, names.arg=names, las=1, main=paste("Relatedness score of property\n", propName, sep=" "), )







# Property별 클래스 ratio의 최대값 (property를 선택할 수 있게) 

library(sqldf)

inputPath <- file.path("C:", "users", "user", "git", "PROPDI", "intermediateResult_ko_2014", "proptypeconf.csv")
inputData = read.csv(inputPath, header=FALSE, sep=",")
col_headings <- c("property", "type", "value")
names(inputData) <- col_headings

inputPath2 <- file.path("C:", "users", "user", "git", "PROPDI", "intermediateResult_ko_2014", "classMaxConf.csv")
inputData2 = read.csv(inputPath2, header=FALSE, sep=",")
col_headings2 <- c("type", "maxvalue")
names(inputData2) <- col_headings2


propName = "^http://ko.dbpedia.org/property/본명$"


newinputdata <- inputData[grep(propName, inputData$property, ignore.case=T),]  

df3 <- sqldf("SELECT property, type, value, maxvalue FROM newinputdata LEFT JOIN inputData2 USING(type)")
df3 <- transform(df3, ratio = value / maxvalue)
df3 <- df3[order(-df3$ratio),]
rank = dim(df3)[1]
names = df3$type

par(mar = c(10, 18, 4, 1))
barplot(df3$ratio, xlab="Relatedness Score",horiz=TRUE, names.arg=names, cex.names=0.7, las=1)
# main=paste("Relatedness score of property\n", propName, sep=" ")
l=paste("Relatedness score of property\n", propName, sep=" ")
mtext(l,side = 3, at = getFigCtr()[1], line = 0.8, cex=1.2) 
# mtext(l,side = 3, at = getFigCtr()[1], line = 0.8, cex=1.5) 





### 각 class별 속하는 프로퍼티 크기 순서대로 정렬
inputPath <- file.path("C:", "users", "user", "git", "PROPDI", "intermediateResult_de_2015", "proptypeconf.csv")
inputData = read.csv(inputPath, header=FALSE, sep=",")
col_headings <- c("property", "type", "value")
names(inputData) <- col_headings

className = "http://dbpedia.org/ontology/President"

newinputdata <- inputData[grep(className, inputData$type, ignore.case=T),]
newinputdata <- newinputdata[order(-newinputdata$value),]

names = newinputdata$property
par(mar = c(10, 18, 4, 1))
barplot(newinputdata$value, xlab="Relatedness Score",horiz=TRUE, names.arg=names, cex.names=0.7, las=1)
# main=paste("Relatedness score of property\n", propName, sep=" ")
l=paste("Relatedness score of properties in class\n", className, sep=" ")
mtext(l,side = 3, at = getFigCtr()[1], line = 0.8, cex=1.2) 



### PROPDI_top_1_result 파일에서 certain class에 배정된 property 리스트 출력
###    (앞으로는 [ratop 계산 이후 최고 precision부터 몇 %만 자르는 것도 만들 수 있을듯 - 미리 계산 해 놓을 필요)
inputPath <- file.path("C:", "users", "user", "git", "PROPDI", "finalResult_de_2015", "PROPDI_top1_result.csv")
inputData = read.csv(inputPath, header=FALSE, sep=",")
col_headings <- c("property", "type")
names(inputData) <- col_headings

className = "http://dbpedia.org/ontology/SpaceMission"

newinputdata <- inputData[grep(className, inputData$type, ignore.case=T),]
newinputdata <- newinputdata[order(newinputdata$property),]

print(newinputdata$property)





# 특정 Property를 사용한 인스턴스 숫자와 속해 있는 클래스간의 관계

library(sqldf)

inputPath <- file.path("C:", "users", "user", "git", "PROPDI", "intermediateResult_de_2015", "proptypeconf.csv")
inputData = read.csv(inputPath, header=FALSE, sep=",")
col_headings <- c("property", "type", "value")
names(inputData) <- col_headings

inputPath2 <- file.path("C:", "users", "user", "git", "PROPDI", "intermediateResult_de_2015", "classMaxConf.csv")
inputData2 = read.csv(inputPath2, header=FALSE, sep=",")
col_headings2 <- c("type", "maxvalue")
names(inputData2) <- col_headings2


propName = "http://de.dbpedia.org/property/sterbedatum"


newinputdata <- inputData[grep(propName, inputData$property, ignore.case=T),]  
newinputdata <- newinputdata[order(-newinputdata$value),]

df3 <- sqldf("SELECT property, type, value, maxvalue FROM newinputdata LEFT JOIN inputData2 USING(type)")
df3 <- transform(df3, ratio = value / maxvalue)
rank = dim(df3)[1]
names = df3$type

par(mar = c(10, 18, 4, 1))
barplot(df3$ratio, xlab="Relatedness Score",horiz=TRUE, names.arg=names, cex.names=0.7, las=1)
# main=paste("Relatedness score of property\n", propName, sep=" ")
l=paste("Relatedness score of property\n", propName, sep=" ")
mtext(l,side = 3, at = getFigCtr()[1], line = 0.8, cex=1.2) 
# mtext(l,side = 3, at = getFigCtr()[1], line = 0.8, cex=1.5) 




chart.Correlation(inputData, histogram=TRUE, pch="+")
