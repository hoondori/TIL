Data Science Practices

Lesson 1 : Modify or even delete defective features

* Remove rows
 * ex) age is 2014
* Set NaN
 * unresponded survey item

Lession 2 : Understand types of data

* Categorical, Binary, Numerical, Ordered, Continous, Discrete


Lession 3 : NA is also information ??

* NA has meanings, has predictability

Lesson 4 : Find out feature singularity

* Missing data
* duplicate data (esp. row)
* constant data (esp. column)
* irregular histogram for categorical feature - left skew, right skew,..
* irregular density function for continuous feature
* irregular distribution for continuous feature on each response(cateogircal) - look out outliers
* histogram of reponse(categorical)

Lesson 5 : Mind of Proper Scaling when it comes to visualization

Technique 1 : Feature Selection

* For each of N features, measure predictability by RMSE score of linear classifier

Technique 2 : Simplify data

* Make categorical or hot-encoding data out of continuous data
* dummy variables - out of categorical variables, sometimes remove less relavant category value

Technique 3 : Imputation

* fill with mean or median
* fill with random selection between mean-std and mean+std

Technique 4 : Make feature from other features

* extract categorical values from other feature's string




