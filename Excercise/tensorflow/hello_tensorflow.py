import tensorflow as tf

hello = tf.constant('Hello, TensoFlow!')

sess = tf.Session()

print sess.run(hello)