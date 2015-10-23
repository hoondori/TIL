
# NumPy 

## Key features

* contiguous allocation in memory
* vectorized operation
* boolean selection
* slice
* reshape
* combine
* split

## create/type/size

* type(m) : 타입 출력, ndarrary
* m.dtype : arrary 내 요소의 타입
* 생성

```python
np.array([1,2,3])
np.array([0]*10)
np.zeros(10)   # float-type 10 zero
np.zeros(10, dtype=int)
np.arrange(0,10,2)  # arrary([0,2,4,6,8])
np.linespace(0,10,11)  # arrary([0.,1., ... 10.])
np.arrary([1,2],[3,4]) # arrary( [[1,2], [3,4]] )
np.size(m)
```

## select

* access via [] operator
* zero-based

```python
m[1,2] # select 1-pos row, 2-pos column elem in 2d-arrary
m[1,]  # 1-pos row, entire column
m[:,2] # entire row, 2-pos column
```

## Boolean operation

```python
m = np.arange(5)
m < 2  # arrary([True,True,False,False,False])
(m<2)|(m>3) # arrary([True,True,False,False,True])
np.vectorize(expr)(m)  # apply boolean functor to arrary
n = m < 2
m[n] # boolean selection
np.sum(m < 2) # count number of True values
```

## Slicing arrarys

## Reshaping

## Combining

## Splitting

## numerical methods







