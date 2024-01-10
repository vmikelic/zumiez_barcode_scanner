import os

os.chdir(os.path.dirname(os.path.abspath(__file__)))
combinedInfo = []
combinedSKU = []
combinedLinks = []

with open('options.txt') as f:
    t = f.readline()
threadCount = int(''.join(filter(str.isdigit, t)))

for x in range(threadCount):
	folderName = 'items'
	scraperFile = folderName+'/items'+str(x)+'.txt'
	itemsFile = open(scraperFile)
	prevLine = ""
	for line in itemsFile:
		if 'SKU:' in line:
			if(not(any(chr.isdigit() for chr in line))):
				continue
			sku = line
			line = prevLine
			name = line
			stock = itemsFile.readline()
			sale = itemsFile.readline()
			itemdigit = sku[5:sku.find('\n')]
			if (len(itemdigit) != 6 and len(itemdigit) != 14):
				raise ValueError('SKU should be either 6 or 14 characters long '+str(len(line)))
			combinedInfo.append(name+sku+stock+sale)
		prevLine = line

for x in range(threadCount):
	folderName = 'sku'
	scraperFile = folderName+'/sku'+str(x)+'.txt'
	itemsFile = open(scraperFile)
	for line in itemsFile:
		if (len(line) != 7 and len(line) != 15):
			raise ValueError('SKU should be either 6 or 14 characters long '+str(len(line)))
		combinedSKU.append(line)

for x in range(threadCount):
	folderName = 'success'
	scraperFile = folderName+'/success'+str(x)+'.txt'
	itemsFile = open(scraperFile)
	for line in itemsFile:
		combinedLinks.append(line)

for x in range(len(combinedSKU)):
	i = combinedInfo[x][combinedInfo[x].find('SKU: '):]
	sku = i[5:i.find('\n')]+'\n'
	if(combinedSKU[x]!=sku):
		raise ValueError('why')

if(len(combinedSKU)!=len(combinedInfo)):
	raise ValueError('why')

zipped = zip(combinedInfo,combinedSKU)	
zipped = list(zipped)

for x in zipped: #checks for consistency
	for item in x[0].split("\n"):
		if "SKU: " in item:
			itemdigit = item[5:]
			if(len(itemdigit) == 0):
				raise ValueError('why')
			if((itemdigit == x[1][:x[1].find('\n')]) == False):
				raise ValueError('info and sku mismatch')

CombinedInfo = open('CombinedInfo.txt', 'w')
for x, _ in zipped:
	if(x.endswith('\n\n')):
		CombinedInfo.write(str(x))
		continue
	CombinedInfo.write(str(x)+'\n')
CombinedInfo.flush()

CombinedSKU = open('CombinedSKU.txt', 'w')
for _, x in zipped:
    CombinedSKU.write(str(x))
CombinedSKU.flush()

CombinedInfo.flush()
CombinedSKU.close()