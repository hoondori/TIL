
# Panda Series

* give numpy array additional functionalities such as indexing, axis labeling, alignment, handling missing data, merging etc.
* one-dimensional labeled indexed array based on the numpy ndarray
* has always an index (default is sequential integers starting from zero)
* access elements not only by integer position, but also by label

## create

```python
s1 = pd.Series(2)
s1 = pd.Series([1,2,3])
s1.values # get values in the Series
s1.index # get labels in the Series

s1 = pd.Series([1,2,3], index=['a','b','c'])
s2 = pd.Series([4,5,6], index = s2.index]) # created with other index
s3 = pd.Series({'a':7,'b':8,'c':9}) # created with python dict

```

## size/shape/uniqueness/count

```python
s1 = pd.Series([0,1,2,np.nan])   # with NaN
len(s1) # which is 4
s1.size # which is 4
s1.shape # which is (4,)
s1.count() # which is 3
s1.unique() # which is [0., 1., 2., nan]
```

## peek/look

```python
s1.head() # peek first 5
s1.head(n=3) # peek first 3
s1.tail() # peek last 5
s1.tail(n=3) # peek last 3
s1.take([0,3,9]) # take those rows indicated by zero-based positions

s1 = pd.Series([1,2,3], index=['a','b','c'])
s1['a'] # by index
s1['a','c']
s1[1] # by position since the index is not an integer

s2 = pd.Series([1,2,3], index=[10,11,12])
s2[11] # by index since the index and value are both integer
s2.loc[12] # by index
s2.iloc[1] by position
s2.ix[12] by index since the index and value are both integer

```

## Alignment/arithmetica operations

* operation 적용 전에 먼저 index 기준으로 series를 정렬한 후 operation 수행
* 한쪽에만 index가 있는 경우에는 NaN 이 된다.
* duplicate label이 있는 경우에는 Cartesian product를 한다.

## NaN

* NaN이 들어있는 series에 operation을 적용하면 exception을 발생시키지 않고 NaN을 무시하고 진행한다.
* 반면에 NaN이 들어있는 numpy arrary은 exception이 발생하여 NaN이 된다.

```python
m = np.arrary([1,2,3,np.NaN])
m.mean() # NaN이 된다.

s1 = pd.Series(m)
s1.mean() # (1+2+3)/3 이 된다.
s1.mean(skipna=False) # NaN 이 된다.
```

## Boolean selection

* boolean selection은 logical expression을 series에 적용해서 새로운 Series of Boolean value를 얻는다.

```python
s1 = pd.Series([0,1,2,3])
s1 > 2 # Series([False, False, False, True])
logicalResults = s1 > 2
s1[logicalResults] # Series([3])
s1[s1>2] # the same as before
s1[s1>2 and s1<=3] # throw exception
s1[(s1>2)&(s1<=3)] # ok!
(s1>=0).all()
(s1>=0).any()
(s1>=0).sum()
```

## Reindexing

* reordering / Inserting NaN / filling missing data

```python
s1 = pd.Series(np.random.randn(3))
s1.index = ['a','b','c']

s1 = pd.Series(np.random.randn(3))
s2 = pd.Series(np.random.randn(3))
combined = pd.concat([s1,s2])
combined.index = np.arange(0,len(combined))

s1 = pd.Series([1,2,3,4], ['a','b','c','d'])
s1.reindex('a','c','g') # become pd.Series([1,3,NaN], ['a','c','g'])

s1.reindex(np.arange(0,7), method='ffill') # forward filling
s1.reindex(np.arange(0,7), method='bfill') # backward filling
```

## Slicing

* not the copy, but the view
* with loc, iloc, ix

```python
s1 = pd.Series(np.arange(100,110), index=np.arange(10,20))
s1[0:6:2] # start-pos, end-pos, interval
s1.iloc[[0,2,4]] # the same as before
s1[:5] # first 5
s1[4:] # from 4th to the end
s1[::-1] # reverse the Series

s2 = pd.Series(np.arange(0,5), index=['a','b','c','d','e'])
s2['b':'d'] # from b to d

```
