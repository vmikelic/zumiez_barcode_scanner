import time as timeT
import requests
import os
import math
from bs4 import BeautifulSoup as Soup
from selenium import webdriver
import selenium.common.exceptions
from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
import threading
from datetime import *
from dateutil.parser import isoparse
from dateutil.parser import parse
import pytz

def get_info(threadNum):
	service = Service()
	options = webdriver.ChromeOptions()
	options.add_argument("--ignore-certificate-error")
	options.add_argument("--ignore-ssl-errors")
	options.add_argument("--headless=new")
	options.add_argument("log-level=3")
	cloud_options = {}
	cloud_options['acceptInsecureCerts'] = True
	cloud_options['acceptSslCerts'] = True
	options.set_capability('cloud:options', cloud_options)
	
	browser = webdriver.Chrome(
		service=service,
        options=options,
    )
	browser.minimize_window()

	exclusionFileName = './exclusion/'+'exclusion'+str(threadNum)+'.txt'
	os.makedirs(os.path.dirname(exclusionFileName), exist_ok=True)
	itemsFileName = './items/'+'items'+str(threadNum)+'.txt'
	os.makedirs(os.path.dirname(itemsFileName), exist_ok=True)
	skuFileName = './sku/'+'sku'+str(threadNum)+'.txt'
	os.makedirs(os.path.dirname(skuFileName), exist_ok=True)
	
	exclusionFile = open('./exclusion/'+'exclusion'+str(threadNum)+'.txt', 'w',newline='\n',encoding='utf-8')
	itemsFile = open('./items/'+'items'+str(threadNum)+'.txt', 'a+',newline='\n',encoding='utf-8')
	skuFile = open('./sku/'+'sku'+str(threadNum)+'.txt', 'a+',newline='\n',encoding='utf-8')

	#while True:
	for u in urls:
		urlString = u
		
		if(urlString.count('/') > 3):
			continue
		if urlString in baseexclusion:
			continue
		browser.get(urlString)
		try:
			obj = WebDriverWait(browser, 30).until(EC.presence_of_element_located((By.CLASS_NAME, "ProductActions")))
			while(obj.text[0:obj.text.find('Size'+'\n')].strip() == ""):
				obj = WebDriverWait(browser, 30).until(EC.presence_of_element_located((By.CLASS_NAME, "ProductActions")))
		except selenium.common.exceptions.TimeoutException as e:
			timeT.sleep(1.0)
			try:
				obj = WebDriverWait(browser, 30).until(EC.presence_of_element_located((By.CLASS_NAME, "ProductActions")))
				while(obj.text[0:obj.text.find('Size'+'\n')].strip() == ""):
					obj = WebDriverWait(browser, 30).until(EC.presence_of_element_located((By.CLASS_NAME, "ProductActions")))
			except selenium.common.exceptions.TimeoutException as e:
					timeT.sleep(1.0)
					try:
						obj = WebDriverWait(browser, 30).until(EC.presence_of_element_located((By.CLASS_NAME, "ProductActions")))
						while(obj.text[0:obj.text.find('Size'+'\n')].strip() == ""):
							obj = WebDriverWait(browser, 30).until(EC.presence_of_element_located((By.CLASS_NAME, "ProductActions")))
					except selenium.common.exceptions.TimeoutException as e:
						timeT.sleep(1.0)
						print(urlString)
						exclusionFile.write(urlString + '\n')
						continue
		itemsFile.write(obj.text[0:obj.text.find('Size'+'\n')]+'\n')
		i = obj.text[obj.text.find('SKU: '):]
		sku = i[5:i.find('\n')]
		skuFile.write(sku+'\n')

os.chdir(os.path.dirname(os.path.abspath(__file__)))

with open('options.txt') as f:
    t = f.readline()

threadCount = int(''.join(filter(str.isdigit, t)))
urls = []
for x in range(threadCount):
	folderName = 'exclusion'
	scraperFile = folderName+'/exclusion'+str(x)+'.txt'
	itemsFile = open(scraperFile)
	for line in itemsFile:
		urls.append(line)

elementPerThread = math.ceil(len(urls)/threadCount)
threads = []

with open('baseexclusion.txt') as f:
		baseexclusion = [line.rstrip() for line in f]

for i in range(threadCount):
	threadloop = threading.Thread(target=get_info, args=(i,))
	threads.append(threadloop)
	threadloop.start()
	timeT.sleep(1.0)

for x in threads:  # iterates over the threads
    x.join()