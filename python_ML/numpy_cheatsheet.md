
# NumPy CheatSheet

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

* access via [ ] operator
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

* retrieve zero or more items
* start:end:step

```python
m = np.arange(1,10)
m[3:8] # start:3, end:8, omit step
m[::2] # every odd item
m[::-1] # reverse order of all items
m[5:] # from 5th to the end
m[:5] # all items in the first 5 positions

# m is 2d narray
m[:,1] # all rows, 1-pos column
m[:,1:3] # all rows, 1-pos to 2-pos columns
m[3:5,:] # all columns, 3-pos to 4-pos rows
```

## Reshaping

* 1-d to 2-d, and vice versa
* 새로운 arrary를 만드는 것이 아니라, dimension을 변형시킨 view만 제공하는 것임
* view의 elem을 고치면 원본도 고쳐진다
* 다만 flatten()은 완전 새로운 array를 리턴한다.

```python
m = np.arange(0,9) # 1x9 1d
n = np.reshape(3,3) # 3x3 2d

n.ravel() # automatically flatten, 3x3 to 1-dim
n.flatten() # flatten return new arrary

n.transpose()
n.T  # same as transpose
```

## Combining

* horizontal, vertical, depth-wise stacking

```python
m = np.arange(9).reshape(3,3)
n = np.arange(9).reshape(3,3)

np.hstack((m,n)) # horizontal stack
np.concatenate((m,n),axis=1) # same as horizontal stack

np.vstack((m,n)) # vertical stack
np.contatenate((m,n),axis=0) # same as vertical stack

np.dstack((m,n)) # depth-wise stack

a = np.arange(9)
b = np.arange(9)
np.column_stack((a,b))
np.row_stack((a,b))
```

## Splitting

* horizontal, vertical, depth-wise split

```python
m = np.arange(12).reshape(3,4)
np.hsplit((m,4)) # horizontal split 2-d arrary into 4 arrary columns
np.vsplit((m,3)) # vertical split 2-d arrary into 3 arrary rows

m = np.arange(27).reshape(3,3,3)
np.dsplit((m,3)) # depth-wise split
```

## numerical methods

* min,max,argmin,argmax
* mean,std,var
* sum,prod,cumsum,cumprod
* all,any
* size,ndim






